package nz.tomasborsje.duskfall.entities.ai;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.attribute.AttributeModifier;
import net.minestom.server.entity.attribute.AttributeOperation;
import net.minestom.server.entity.pathfinding.Navigator;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.time.Cooldown;
import net.minestom.server.utils.time.TimeUnit;
import nz.tomasborsje.duskfall.entities.MmoCreature;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

public class MeleeAttackTargetOrEvadeGoal extends GoalSelector {

    private final Cooldown cooldown = new Cooldown(Duration.of(5, TimeUnit.SERVER_TICK));

    AttributeModifier evadeSpeed = new AttributeModifier(NamespaceID.from("duskfall", "evadespeed"), 2.0, AttributeOperation.ADD_VALUE);

    private long lastHit;
    private final Pos spawnPos;
    private boolean resetting;
    private final double range;
    private final double leashRange;
    private final Duration delay;

    private boolean stop;
    private Entity cachedTarget;

    /**
     * @param entityCreature the entity to add the goal to
     * @param attackRange          the allowed range the entity can attack others.
     * @param delay          the delay between each attacks
     * @param timeUnit       the unit of the delay
     */
    public MeleeAttackTargetOrEvadeGoal(@NotNull MmoCreature entityCreature, Pos spawnPos, double attackRange, double leashRange, int delay, @NotNull TemporalUnit timeUnit) {
        this(entityCreature, spawnPos, attackRange, leashRange, Duration.of(delay, timeUnit));
    }

    /**
     * @param entityCreature the entity to add the goal to
     * @param attackRange          the allowed range the entity can attack others.
     * @param delay          the delay between each attacks
     */
    public MeleeAttackTargetOrEvadeGoal(@NotNull MmoCreature entityCreature, Pos spawnPos, double attackRange, double leashRange, Duration delay) {
        super(entityCreature);
        this.spawnPos = spawnPos;
        this.range = attackRange;
        this.leashRange = leashRange;
        this.delay = delay;
    }

    @Override
    public boolean shouldStart() {
        this.cachedTarget = findTarget();
        return this.cachedTarget != null;
    }

    @Override
    public void start() {
        final Point targetPosition = this.cachedTarget.getPosition();
        resetting = false;
        entityCreature.getNavigator().setPathTo(targetPosition);
    }

    @Override
    public void tick(long time) {
        Entity target;
        if (this.cachedTarget != null) {
            target = this.cachedTarget;
            this.cachedTarget = null;
        } else {
            target = findTarget();
        }

        Navigator navigator = entityCreature.getNavigator();

        // Stop if we have no target or we've returned to spawn after resetting
        this.stop = target == null || (resetting && entityCreature.getPosition().samePoint(spawnPos));

        // Stop if our target is in a different instance
        if(target != null && target.getInstance() != entityCreature.getInstance()) {
            stop = true;
        }

        if (!stop) {

            // Attack the target entity if they're not too far from our spawn and we're not resetting
            if (!resetting && entityCreature.getDistanceSquared(target) <= range * range) {
                entityCreature.lookAt(target);
                if (!Cooldown.hasCooldown(time, lastHit, delay) && !entityCreature.isDead()) {
                    entityCreature.attack(target, true);
                    this.lastHit = time;
                }
                return;
            }

            // Move toward the target entity if they're in range, else reset
            if(!resetting && entityCreature.getDistanceSquared(spawnPos) <= leashRange * leashRange) {
                final var pathPosition = navigator.getPathPosition();
                final var targetPosition = target.getPosition();
                if (pathPosition == null || !pathPosition.samePoint(targetPosition)) {
                    if (this.cooldown.isReady(time)) {
                        this.cooldown.refreshLastUpdate(time);
                        navigator.setPathTo(targetPosition);
                    }
                }
            }
            // Out of range, move back to spawn position
            else {
                final var pathPosition = navigator.getPathPosition();
                if (pathPosition == null || !pathPosition.samePoint(spawnPos)) {
                    if (this.cooldown.isReady(time)) {
                        this.cooldown.refreshLastUpdate(time);
                        navigator.setPathTo(spawnPos);

                        // Reset entity
                        resetting = true;
                        // TODO: Reset health?
                        entityCreature.getAttribute(Attribute.MOVEMENT_SPEED).addModifier(evadeSpeed);
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldEnd() {
        return stop;
    }

    @Override
    public void end() {
        // Exit combat and heal to full
        if(entityCreature instanceof MmoCreature mmoCreature) {
            mmoCreature.forceExitCombat();
            mmoCreature.getStats().healToFull();
        }

        // Stop following the target
        entityCreature.getNavigator().setPathTo(null);
        entityCreature.getAttribute(Attribute.MOVEMENT_SPEED).removeModifier(evadeSpeed);
        entityCreature.setTarget(null);
    }
}
