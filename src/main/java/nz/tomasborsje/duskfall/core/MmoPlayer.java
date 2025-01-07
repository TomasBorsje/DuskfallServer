package nz.tomasborsje.duskfall.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.network.PlayerProvider;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import nz.tomasborsje.duskfall.DuskfallServer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MmoPlayer extends Player implements PlayerProvider, MmoEntity {
    private final StatContainer stats;
    private boolean shouldRecalculateStats = true;

    public MmoPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);
        // TODO: Load stats from DB
        int level = new Random().nextInt(5, 50);
        stats = new StatContainer(this, level);
        DuskfallServer.logger.info("Player object created with random level "+level);
    }

    @Override
    public @NotNull StatContainer getStats() {
        // If our current stats are outdated, recalculate
        if(shouldRecalculateStats) {
            stats.recalculateStats();
            shouldRecalculateStats = false;
        }
        return stats;
    }

    @Override
    public @NotNull List<StatModifier> getStatModifiers() {
        // TODO: Cache this (don't think it's very expensive tbh)
        List<StatModifier> list = new ArrayList<>(5);

        // Get stat modifiers from all itemstacks
        ItemBasedStatModifier helmetModifier = new ItemBasedStatModifier(inventory.getEquipment(EquipmentSlot.HELMET, (byte)0));
        ItemBasedStatModifier chestplateModifier = new ItemBasedStatModifier(inventory.getEquipment(EquipmentSlot.CHESTPLATE, (byte)0));
        ItemBasedStatModifier leggingsModifier = new ItemBasedStatModifier(inventory.getEquipment(EquipmentSlot.LEGGINGS, (byte)0));
        ItemBasedStatModifier bootsModifier = new ItemBasedStatModifier(inventory.getEquipment(EquipmentSlot.BOOTS, (byte)0));
        ItemBasedStatModifier heldModifier = new ItemBasedStatModifier(inventory.getItemStack(getHeldSlot()));

        list.add(helmetModifier);
        list.add(chestplateModifier);
        list.add(leggingsModifier);
        list.add(bootsModifier);
        list.add(heldModifier);

        return list;
    }

    @Override
    public void tick(long time) {
        super.tick(time);

        // TODO: Decide when stats are outdated (item equip, buff added, damage taken, etc.)
        shouldRecalculateStats = true;
        // If our current stats are outdated, recalculate
        if(shouldRecalculateStats) {
            stats.recalculateStats();
            shouldRecalculateStats = false;
        }

        // Health regen (1/tick)
        stats.gainHealth(1);

        sendMessage(Component.text("Your melee damage is: "+stats.getMeleeDamage()));

        // Show health to player
        Component healthBar = Component.text("\u2764 ", NamedTextColor.RED)
                .append(Component.text(stats.getCurrentHealth() + " / " + stats.getMaxHealth(), NamedTextColor.WHITE));
        sendActionBar(healthBar);
    }

    @Override
    public void hurt(DamageInstance damageInstance) {
        int damageTaken = stats.takeDamage(damageInstance.type, damageInstance.amount);
        // If we took actual damage - note damage taken is 0 if we're already dead
        if(damageTaken >= 0) {
            // Cosmetic hurt effect
            this.damage(DamageType.GENERIC, 0.01f);

            if(stats.isDead()) {
                kill(damageInstance);
            }
        }
    }

    @Override
    public void kill(DamageInstance killingBlow) {
        DuskfallServer.logger.info(getUsername()+" died!");
        sendMessage(Component.text("You died!", NamedTextColor.RED));
        teleport(new Pos(0, 46, 0));

        // TODO: Remove buffs, etc.

        stats.recalculateStats();
        stats.healToFull();
    }

    @Override
    public @NotNull Player createPlayer(@NotNull PlayerConnection connection, @NotNull GameProfile gameProfile) {
        return new MmoPlayer(connection, gameProfile);
    }
}
