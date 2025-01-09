package nz.tomasborsje.duskfall.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.goal.MeleeAttackGoal;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.entity.ai.target.LastEntityDamagerTarget;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.DamageEventPacket;
import net.minestom.server.network.packet.server.play.EntityAnimationPacket;
import net.minestom.server.utils.time.TimeUnit;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.registry.ItemRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MmoCreature extends EntityCreature implements MmoEntity {
    private final StatContainer stats;

    // TODO: Take in an EntityDef instead of a type + level - for data-driven entities
    public MmoCreature(@NotNull EntityType entityType, int level) {
        super(entityType);
        this.stats = new StatContainer(this, level);

        // TODO: MMO based AI
        // E.g. Attack last hit target, else roam around a given position
        addAIGroup(
                List.of(
                        new MeleeAttackGoal(this, 1.6, 20, TimeUnit.SERVER_TICK), // Attack the target
                        new RandomStrollGoal(this, 5) // Walk around
                ),
                List.of(
                        new LastEntityDamagerTarget(this, 32), // First target the last entity which attacked you
                        new ClosestEntityTarget(this, 32, entity -> entity instanceof Player) // If there is none, target the nearest player
                )
        );

        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.1);

        // Set entity meta
        EntityMeta meta = getEntityMeta();
        meta.setNotifyAboutChanges(false);
        setEntityMeta(meta);
        meta.setNotifyAboutChanges(true);
    }

    @Override
    public void tick(long time) {
        super.tick(time);

    }

    @Override
    public void hurt(DamageInstance damageInstance) {
        int damageTaken = stats.takeDamage(damageInstance.type, damageInstance.amount);
        if (damageTaken >= 0) {
            this.damage(DamageType.GENERIC, 0.01f); // Cosmetic damage indicator TODO: Packet instead?
            this.heal();
            if(stats.isDead()) {
                kill(damageInstance);
            } else {
                // Set target to attacker
                setTarget(damageInstance.owner.asEntity());
            }
        }
        updateEntityMeta();
    }

    @Override
    public void kill(DamageInstance killingBlow) {
        if (killingBlow.owner instanceof MmoPlayer player) {
            // If a player killed me, send them loot
            // TODO: Loot and loot tables
            player.sendMessage("Congrats on killing me (" + getClass().getSimpleName() + "), dude!");
            player.levelUp();

            ItemStack loot = ItemRegistry.GetRandomItem().buildItemStack();
            player.getInventory().addItemStack(loot);
        }
        kill();
    }

    @Override
    public Entity asEntity() {
        return this;
    }

    @Override
    public @NotNull List<StatModifier> getStatModifiers() {
        // TODO: Get all
        return List.of();
    }

    /**
     * Set all required changes to the EntityMeta. The type of the EntityMeta will correspond to the EntityType.
     *
     * @param meta This entity's EntityMeta
     */
    protected void setEntityMeta(EntityMeta meta) {
        meta.setCustomName(Component.text("[" + stats.getLevel() + "] Dangerous Zombie", NamedTextColor.RED));
        meta.setCustomNameVisible(true);
        meta.setOnFire(true);
    }

    protected void updateEntityMeta() {
        entityMeta.setCustomName(Component.text("[" + stats.getLevel() + "] Dangerous Zombie (" + stats.getCurrentHealth() + "/" + stats.getMaxHealth() + ")", NamedTextColor.RED));
    }

    @Override
    public @NotNull StatContainer getStats() {
        return stats;
    }
}
