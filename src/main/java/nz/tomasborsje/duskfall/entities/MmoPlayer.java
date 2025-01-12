package nz.tomasborsje.duskfall.entities;

import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.PlayerProvider;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.buffs.Buff;
import nz.tomasborsje.duskfall.core.*;
import nz.tomasborsje.duskfall.database.PlayerData;
import nz.tomasborsje.duskfall.sounds.Sounds;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MmoPlayer extends Player implements PlayerProvider, MmoEntity {
    private final List<Buff> buffs = new ArrayList<>();
    private final StatContainer stats;
    private boolean shouldRecalculateStats = true;

    public MmoPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        this(playerConnection, gameProfile, DuskfallServer.dbConnection.loadPlayerData(gameProfile.name()));
    }

    public MmoPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile, @NotNull PlayerData data) {
        super(playerConnection, gameProfile);

        // Populate inventory with items from playerdata TODO: Remove glitched items
        BinaryTagIO.Reader reader = BinaryTagIO.reader();
        for (Map.Entry<String, String> entry : data.inventoryItems.entrySet()) {
            try {
                int slot = Integer.parseInt(entry.getKey());
                ItemStack stack = ItemStack.fromItemNBT(reader.read(IOUtils.toInputStream(entry.getValue())));
                inventory.setItemStack(slot, stack);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        stats = new StatContainer(this, data.level);
        DuskfallServer.logger.info("Player object created with loaded level {}", stats.getLevel());
    }

    @Override
    public void tick(long time) {
        super.tick(time);

        // Tick all buffs then remove any marked for removal
        buffs.forEach(Buff::tick);
        buffs.removeIf(buff -> {
            if (buff.shouldRemove()) {
                buff.onRemove();
                return true;
            }
            return false;
        });

        // TODO: Decide when stats are outdated (item equip, buff added, damage taken, etc.)
        shouldRecalculateStats = true;
        // If our current stats are outdated, recalculate
        if (shouldRecalculateStats) {
            stats.recalculateStats();
            this.setLevel(stats.getLevel());
            shouldRecalculateStats = false;
        }

        // Health regen (1/tick)
        stats.gainHealth(1);

        renderPlayerUi();
    }

    @Override
    public void hurt(DamageInstance damageInstance) {
        int damageTaken = stats.takeDamage(damageInstance.type, damageInstance.amount);
        // If we took actual damage - note damage taken is 0 if we're already dead
        if (damageTaken >= 0) {
            // Cosmetic hurt effect
            this.damage(DamageType.GENERIC, 0.01f);

            if (stats.isDead()) {
                kill(damageInstance);
            }
        }
    }

    @Override
    public void kill(DamageInstance killingBlow) {
        // Remove buffs
        buffs.forEach(buff -> buff.onOwnerDie(killingBlow));
        buffs.clear();
        // Reset stats
        stats.recalculateStats();
        stats.healToFull();

        DuskfallServer.logger.info("{} died!", getUsername());
        sendMessage(Component.text("You died!", NamedTextColor.RED));
        teleport(new Pos(0, 46, 0));
    }

    @Override
    public void addBuff(@NotNull Buff newBuff) {
        // Replace buff if it exists, and should be replaced
        if (newBuff.shouldReplaceExisting()) {
            buffs.removeIf(buff -> buff.getId().equals(newBuff.getId()));
        }
        buffs.add(newBuff);
        DuskfallServer.logger.info("Player gained buff {}", newBuff.getId());
    }

    /**
     * Increases the player's level and fully restores their health and mana.
     */
    public void levelUp() {
        stats.setLevel(stats.getLevel() + 1);
        stats.recalculateStats();
        stats.healToFull();

        playSound(Sounds.LEVEL_UP);
        sendMessage(Component.text("You have reached level " + stats.getLevel() + "!", NamedTextColor.GOLD));
    }

    /**
     * Grant an ItemStack to the player with the specified item gain reason.
     * A chat message will be displayed informing the player of the item they received.
     * If the player's inventory is full, they will receive the item in the mail instead.
     *
     * @param itemStack The item stack to grant
     * @param reason    The reason they are receiving this item stack
     */
    public void giveItem(ItemStack itemStack, ItemGainReason reason) {
        // Add item to inventory
        boolean addedSuccessfully = inventory.addItemStack(itemStack);

        if (!addedSuccessfully) {
            // TODO: Send to mailbox instead, etc.
        } else {
            Component itemName = itemStack.get(ItemComponent.CUSTOM_NAME);
            if (itemName == null) {
                itemName = Component.text("null");
            }

            // Append lore lines
            Component hoverText = itemName.appendNewline();
            List<Component> lore = itemStack.get(ItemComponent.LORE);
            if (lore != null) {
                for (Component loreLine : lore) {
                    hoverText = hoverText.append(loreLine);
                }
            }
            // Show message in chat informing player of item gain
            sendMessage(Component.text("You " + reason.gainVerb + " ", NamedTextColor.GRAY).append(Component.text(itemStack.amount() + "x ", NamedTextColor.WHITE)).append(itemName).hoverEvent(hoverText) // Show item description on hover
                    .append(Component.text(".", NamedTextColor.GRAY)));

            // Play sound
            playSound(Sounds.LOOT_BAG_RUSTLE);
        }
    }

    /**
     * Renders and updates the player's MMO HUD (health bar, stats above hot-bar, scoreboard, etc.).
     */
    private void renderPlayerUi() {
        // Set health bar to display player health
        setHealth(stats.getCurrentHealth() / (float) stats.getMaxHealth() * 19 + 1); // TODO: Interrupt play out packet?

        // Show health to player
        Component healthBar = Component.text(TextIcons.HEART+" ", NamedTextColor.RED).append(Component.text(stats.getCurrentHealth() + " / " + stats.getMaxHealth() + " Melee: " + stats.getMeleeDamage(), NamedTextColor.WHITE));
        sendActionBar(healthBar);
    }

    @Override
    public @NotNull List<StatModifier> getStatModifiers() {
        // TODO: Cache this (don't think it's very expensive tbh)
        List<StatModifier> list = new ArrayList<>(5);

        // Get stat modifiers from all itemstacks
        ItemBasedStatModifier helmetModifier = new ItemBasedStatModifier(inventory.getEquipment(EquipmentSlot.HELMET, (byte) 0));
        ItemBasedStatModifier chestplateModifier = new ItemBasedStatModifier(inventory.getEquipment(EquipmentSlot.CHESTPLATE, (byte) 0));
        ItemBasedStatModifier leggingsModifier = new ItemBasedStatModifier(inventory.getEquipment(EquipmentSlot.LEGGINGS, (byte) 0));
        ItemBasedStatModifier bootsModifier = new ItemBasedStatModifier(inventory.getEquipment(EquipmentSlot.BOOTS, (byte) 0));
        ItemBasedStatModifier heldModifier = new ItemBasedStatModifier(inventory.getItemStack(getHeldSlot()));
        list.add(helmetModifier);
        list.add(chestplateModifier);
        list.add(leggingsModifier);
        list.add(bootsModifier);
        list.add(heldModifier);

        // Add all stat modifying buffs
        list.addAll(buffs.stream().filter(StatModifier.class::isInstance).map(StatModifier.class::cast).toList());

        return list;
    }

    @Override
    public @NotNull StatContainer getStats() {
        // If our current stats are outdated, recalculate
        if (shouldRecalculateStats) {
            stats.recalculateStats();
            shouldRecalculateStats = false;
        }
        return stats;
    }

    /**
     * Build a PlayerData object containing this player's information.
     *
     * @return PlayerData object containing this player's information.
     */
    public PlayerData getPlayerData() {
        Map<String, String> inventoryMap = new HashMap<>();
        BinaryTagIO.Writer writer = BinaryTagIO.writer();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack slotStack = inventory.getItemStack(slot);

            if (!slotStack.isAir()) {
                try {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    writer.write(slotStack.toItemNBT(), stream);
                    inventoryMap.put(String.valueOf(slot), stream.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return new PlayerData(getUsername(), stats.getLevel(), inventoryMap);
    }

    @Override
    public @NotNull Player createPlayer(@NotNull PlayerConnection connection, @NotNull GameProfile gameProfile) {
        String username = gameProfile.name();
        PlayerData playerData = DuskfallServer.dbConnection.loadPlayerData(username);
        return new MmoPlayer(connection, gameProfile, playerData);
    }

    @Override
    public Entity asEntity() {
        return this;
    }
}
