package nz.tomasborsje.duskfall.events;

import net.minestom.server.event.EventListener;
import net.minestom.server.event.entity.EntityAttackEvent;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.core.*;
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
        if(event.getTarget() instanceof MmoEntity victim && event.getEntity() instanceof MmoEntity attacker) {

            victim.hurt(new DamageInstance(
                    MmoDamageCause.ENTITY_ATTACK,
                    MmoDamageType.PHYSICAL,
                    (MmoEntity) event.getEntity(),
                    attacker.getStats().getMeleeDamage()));

            DuskfallServer.logger.info(event.getTarget().getClass().getSimpleName()+" with level "+victim.getStats().getLevel()+" was struck for "+attacker.getStats().getMeleeDamage()+"! They are "+victim.getStats().getCurrentHealth()+" hp");
        }
        return Result.SUCCESS;
    }
}
