package nz.tomasborsje.duskfall.core;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.IntBinaryTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.goal.MeleeAttackGoal;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.entity.ai.target.LastEntityDamagerTarget;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MmoCreature extends EntityCreature implements IMmoEntity {
    private final StatContainer stats;

    // TODO: Take in an EntityDef instead of a type + level - for data-driven entities
    public MmoCreature(@NotNull EntityType entityType, int level) {
        super(entityType);
        this.stats = new StatContainer(level);
        // TODO: MMO based AI
        // E.g. Attack last hit target, else roam around a given position
        addAIGroup(
                List.of(
                        new MeleeAttackGoal(this, 1.6, 20, TimeUnit.SERVER_TICK), // Attack the target
                        new RandomStrollGoal(this, 20) // Walk around
                ),
                List.of(
                        new LastEntityDamagerTarget(this, 32), // First target the last entity which attacked you
                        new ClosestEntityTarget(this, 32, entity -> entity instanceof Player) // If there is none, target the nearest player
                )
        );

        // Set entity meta
        EntityMeta meta = getEntityMeta();
        meta.setNotifyAboutChanges(false);
        setEntityMeta(meta);
        meta.setNotifyAboutChanges(true);
    }

    @Override
    public void hurt(DamageInstance damageInstance) {
        int damageTaken = stats.takeDamage(damageInstance.type, damageInstance.amount);
        if (damageTaken >= 0) {
            this.damage(DamageType.GENERIC, 0.01f);
            this.heal();
            if(!stats.isAlive()) {
                kill(damageInstance);
            }
        }
        updateEntityMeta();
    }

    @Override
    public void kill(DamageInstance killingBlow) {
        if (killingBlow.owner instanceof Player player) {
            // If a player killed me, send them loot
            player.sendMessage("Congrats on killing me (" + getClass().getSimpleName() + "), dude!");

            CompoundBinaryTag tag = CompoundBinaryTag.builder().put("stamina", IntBinaryTag.intBinaryTag(5)).build();

            ItemStack loot = ItemStack.builder(Material.AMETHYST_SHARD)
                    .glowing()
                    .set(ItemStackTags.MMO_DATA, tag)
                    .customName(Component.text("Shard of Stamina"))
                    .lore(Component.text("+5 Stamina", NamedTextColor.AQUA))
                    .build();

            player.getInventory().addItemStack(loot);
        }
        kill();
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
