package nz.tomasborsje.duskfall.core;

import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.goal.MeleeAttackGoal;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.entity.ai.target.LastEntityDamagerTarget;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MmoCreature extends EntityCreature {
    public MmoCreature(@NotNull EntityType entityType) {
        super(entityType);
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
    }
}
