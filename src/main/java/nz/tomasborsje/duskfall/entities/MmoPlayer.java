package nz.tomasborsje.duskfall.entities;

import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MmoPlayer extends Player implements PlayerProvider, MmoEntity {
    private final PlayerUi ui;
    private final StatContainer stats;
    private final List<Buff> buffs = new ArrayList<>();

    private boolean shouldRecalculateStats = true;

    public MmoPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        this(playerConnection, gameProfile, DuskfallServer.dbConnection.loadPlayerData(gameProfile.name()));
    }

    public MmoPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile, @NotNull PlayerData data) {
        super(playerConnection, gameProfile);

        // Populate inventory with items from playerdata
        // TODO: Remove glitched items
        BinaryTagIO.Reader reader = BinaryTagIO.reader();
        for (Map.Entry<String, String> entry : data.inventoryItems.entrySet()) {
            try {
                int slot = Integer.parseInt(entry.getKey());
                ItemStack stack = ItemStack.fromItemNBT(reader.read(IOUtils.toInputStream(entry.getValue(), Charset.defaultCharset())));
                inventory.setItemStack(slot, stack);
            } catch (Exception e) {
                DuskfallServer.logger.error("Could not deserialize item NBT for slot {}: {}", entry.getKey(), e);
            }
        }

        stats = new StatContainer(this, data.level);
        ui = new PlayerUi(this); // Ui relies on stats
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

        ui.render();
    }

    @Override
    public int hurt(@NotNull DamageInstance damageInstance) {
        int damageTaken = stats.takeDamage(damageInstance.type, damageInstance.amount);
        // If we took actual damage - note damage taken is 0 if we're already dead
        if (damageTaken >= 0) {
            // Cosmetic hurt effect
            this.damage(DamageType.GENERIC, 0.01f);

            if (stats.isDead()) {
                kill(damageInstance);
            }
        }

        return damageTaken;
    }

    @Override
    public void kill(@NotNull DamageInstance killingBlow) {
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
    public void heal(int amount) {
        stats.gainHealth(amount);
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
                itemName = Component.text("null", NamedTextColor.WHITE);
            }

            // Append lore lines
            Component hoverText = itemName;
            List<Component> lore = itemStack.get(ItemComponent.LORE);
            if (lore != null) {
                for (Component component : lore) {
                    hoverText = hoverText.appendNewline().append(component);
                }
            }

            Style itemNameStyle = itemName.style();
            Component itemNameDisplay = Component.text("[", itemNameStyle).append(itemName).append(Component.text("]", itemNameStyle));

            // Show message in chat informing player of item gain
            sendMessage(Component.text("You " + reason.gainVerb + " ", NamedTextColor.GRAY).append(Component.text(itemStack.amount() + "x ", NamedTextColor.WHITE)).append(itemNameDisplay).hoverEvent(hoverText) // Show item description on hover
                    .append(Component.text(".", NamedTextColor.GRAY)));

            // Play sound
            playSound(Sounds.LOOT_BAG_RUSTLE);
        }
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
    public @NotNull String getMmoName() {
        return getUsername();
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
