package nz.tomasborsje.duskfall.entities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.utils.time.TimeUnit;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.buffs.Buff;
import nz.tomasborsje.duskfall.core.DamageInstance;
import nz.tomasborsje.duskfall.core.StatContainer;
import nz.tomasborsje.duskfall.core.StatModifier;
import nz.tomasborsje.duskfall.definitions.entities.EntityDefinition;
import nz.tomasborsje.duskfall.entities.ai.EntityCurrentTarget;
import nz.tomasborsje.duskfall.entities.ai.MeleeAttackTargetOrEvadeGoal;
import nz.tomasborsje.duskfall.entities.ai.RoamAroundSpawnGoal;
import nz.tomasborsje.duskfall.registry.Registries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MmoCreature extends EntityCreature implements MmoEntity {
    private final List<Buff> buffs = Collections.synchronizedList(new ArrayList<>());
    private final EntityDefinition def;
    private final Pos spawnPos;
    private final StatContainer stats;
    private String name;

    private boolean inCombat = false;
    private boolean shouldRecalculateStats;

    /**
     * Creates a new MmoCreature defined by the values in the given EntityDefinition.
     * The spawn position is passed through as well.
     *
     * @param def      The entity definition to build an MmoCreature for.
     * @param spawnPos The position to spawn this entity at when it's added to an instance.
     */
    public MmoCreature(EntityDefinition def, Pos spawnPos) {
        super(EntityType.fromNamespaceId(def.getEntityType()));
        this.def = def;
        this.spawnPos = spawnPos;
        this.stats = new StatContainer(this, def.getLevel());
        this.name = def.getName(); // Snapshot name so we can change it later if desired

        // Add AI (Chase down target if we have one, else roam spawn)
        addAIGroup(
                // Behaviour
                switch (def.getAiType()) {
                    // Neutral and aggressive creatures attack their target or roam
                    case AGGRESSIVE, NEUTRAL -> List.of(
                            new MeleeAttackTargetOrEvadeGoal(this, spawnPos, 1.6, 20, 20, TimeUnit.SERVER_TICK),
                            new RoamAroundSpawnGoal(this, spawnPos, 5)
                    );
                    // Passive creatures roam
                    default -> List.of( new RoamAroundSpawnGoal(this, spawnPos, 5));
                },
                // Targeting
                switch (def.getAiType()) {
                    // Aggressive creatures target their internal target or the nearest player
                    case AGGRESSIVE -> List.of(
                            new EntityCurrentTarget(this), // First target the last entity which attacked you
                            new ClosestEntityTarget(this, 32, entity -> entity instanceof MmoPlayer) // Else target the nearest player
                    );
                    // Neutral creatures only target their internal target, if any
                    case NEUTRAL -> List.of(
                            new EntityCurrentTarget(this) // Target the last entity which attacked you
                    );
                    // Passive creatures don't target anything
                    default -> List.of();
                }
        );


        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.1);

        // Set entity meta
        EntityMeta meta = getEntityMeta();
        meta.setNotifyAboutChanges(false);
        setEntityMeta();
        meta.setNotifyAboutChanges(true);
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
            shouldRecalculateStats = false;
        }
    }

    @Override
    public void hurt(@NotNull DamageInstance damageInstance) {
        if (!inCombat) {
            enterCombat();
        }
        inCombat = true;
        int damageTaken = stats.takeDamage(damageInstance.type, damageInstance.amount);
        if (damageTaken >= 0) {
            this.damage(DamageType.GENERIC, 0.01f);
            this.heal();
            if (stats.isDead()) {
                kill(damageInstance);
            } else {
                if (damageInstance.owner != null) {
                    // Set target to attacker
                    setTarget(damageInstance.owner.asEntity());
                }
            }
        }
        updateEntityMeta();
    }

    @Override
    public void kill(@NotNull DamageInstance killingBlow) {
        // TODO: Acquirable instead to avoid concurrent modification exception
        acquirable().sync(entity -> {
            buffs.forEach(buff -> buff.onOwnerDie(killingBlow));
            buffs.clear();
        });

        if (killingBlow.owner instanceof MmoPlayer player) {
            // If a player killed me, send them loot
            // TODO: Loot and loot tables
            player.sendMessage("Congrats on killing me (" + name + "), dude!");
            player.levelUp();

            LootBagEntity lootBag = new LootBagEntity(Component.text(getMmoName()), Registries.ITEMS.getRandomItem().buildItemStack(), Registries.ITEMS.getRandomItem().buildItemStack(), Registries.ITEMS.getRandomItem().buildItemStack());
            lootBag.setInstance(instance, position);
        }
        kill();
        inCombat = false;
    }

    @Override
    public void spawn() {
        super.spawn();
        this.teleport(spawnPos);
    }

    public void enterCombat() {
        setCustomNameVisible(true);
    }

    public void exitCombat() {
        setCustomNameVisible(false);
    }

    @Override
    public void addBuff(@NotNull Buff newBuff) {
        // Replace buff if it exists, and should be replaced
        if (newBuff.shouldReplaceExisting()) {
            buffs.removeIf(buff -> buff.getId().equals(newBuff.getId()));
        }
        buffs.add(newBuff);
        DuskfallServer.logger.info("Creature gained buff {}", newBuff.getId());
    }

    @Override
    public Entity asEntity() {
        return this;
    }

    @Override
    public @NotNull List<StatModifier> getStatModifiers() {
        return buffs.stream()
                .filter(StatModifier.class::isInstance)
                .map(StatModifier.class::cast)
                .toList();
    }

    @Override
    public @NotNull String getMmoName() {
        return name;
    }

    /**
     * Set all required changes to the EntityMeta. The type of the EntityMeta will correspond to the EntityType.
     */
    protected void setEntityMeta() {
        entityMeta.setCustomName(buildDisplayName());
    }

    protected void updateEntityMeta() {
        entityMeta.setCustomName(buildDisplayName());
    }

    /**
     * Get the entity's display name. This will be shown above their head if they are looked at, or in combat.
     *
     * @return The component to set the entity's display name to
     */
    protected Component buildDisplayName() {
        // TODO: Proper entity names
        Component levelDisplay = Component.text("[", NamedTextColor.WHITE).append(Component.text(stats.getLevel(), NamedTextColor.BLUE).append(Component.text("] ", NamedTextColor.WHITE)));
        Component nameDisplay = Component.text(name, NamedTextColor.RED).append(Component.text(" (" + stats.getCurrentHealth() + "/" + stats.getMaxHealth() + ")", NamedTextColor.WHITE));
        return levelDisplay.append(nameDisplay);
    }

    @Override
    public @NotNull StatContainer getStats() {
        return stats;
    }
}
