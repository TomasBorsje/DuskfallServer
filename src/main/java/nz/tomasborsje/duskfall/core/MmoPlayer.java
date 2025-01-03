package nz.tomasborsje.duskfall.core;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.color.Color;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.PlayerProvider;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import nz.tomasborsje.duskfall.DuskfallServer;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class MmoPlayer extends Player implements PlayerProvider, IMmoEntity {
    private final StatContainer stats;
    public MmoPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);
        int level = new Random().nextInt(5, 50);
        stats = new StatContainer(level);
        DuskfallServer.logger.info("Player object created with random level "+level);
    }

    @Override
    public void tick(long time) {
        super.tick(time);
        // Show health to player
        Component healthBar = Component.text("\u2764 ", NamedTextColor.RED)
                .append(Component.text(stats.getCurrentHealth() + " / " + stats.getMaxHealth(), NamedTextColor.WHITE));
        sendActionBar(healthBar);

        // Get held item
        ItemStack held = this.inventory.getItemStack(this.getHeldSlot());

        if(!held.isAir()) {
            sendMessage("Held item type: "+held.material().name());

            // Get tag and show
            CompoundBinaryTag tag = (CompoundBinaryTag) held.getTag(ItemStackTags.MMO_DATA);

            if(tag != null) {
                sendMessage("Your stamina: " + tag.getInt(ItemStackKeys.STAMINA));
            }
        }
    }

    @Override
    public void hurt(DamageInstance damageInstance) {
        int damageTaken = stats.takeDamage(damageInstance.type, damageInstance.amount);
        // If we took actual damage - note damage taken is 0 if we're already dead
        if(damageTaken >= 0) {
            // Cosmetic hurt effect
            this.damage(DamageType.GENERIC, 0.01f);

            if(!stats.isAlive()) {
                kill(damageInstance);
            }
        }
    }

    @Override
    public void kill(DamageInstance killingBlow) {
        DuskfallServer.logger.info(getUsername()+" died!");
        sendMessage("You freakin' died!");
        teleport(new Pos(0, 40, 0));
        stats.reset();
    }

    @Override
    public @NotNull Player createPlayer(@NotNull PlayerConnection connection, @NotNull GameProfile gameProfile) {
        return new MmoPlayer(connection, gameProfile);
    }

    @Override
    public @NotNull StatContainer getStats() {
        return stats;
    }
}
