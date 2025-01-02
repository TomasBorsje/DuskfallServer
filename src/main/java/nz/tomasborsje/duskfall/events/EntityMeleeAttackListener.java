package nz.tomasborsje.duskfall.events;

import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.entity.EntityAttackEvent;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.core.MmoPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player does a left click on an entity or with net.minestom.server.entity.EntityCreature.attack(Entity).
 */
public class EntityMeleeAttackListener implements EventListener<EntityAttackEvent> {
    @Override
    public @NotNull Class<EntityAttackEvent> eventType() {
        return EntityAttackEvent.class;
    }

    @NotNull
    @Override
    public Result run(@NotNull EntityAttackEvent event) {
        if(event.getTarget() instanceof MmoPlayer mmoPlayer) {
            DuskfallServer.logger.info("Player with random number "+mmoPlayer.random+" was struck!");
            mmoPlayer.damage(DamageType.FALL, 4);
        }
        else if(event.getTarget() instanceof LivingEntity living) {
            living.damage(DamageType.GENERIC, 4);
        }

        return Result.SUCCESS;
    }

}
