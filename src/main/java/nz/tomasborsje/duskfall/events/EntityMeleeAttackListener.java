package nz.tomasborsje.duskfall.events;

import net.minestom.server.event.EventListener;
import net.minestom.server.event.entity.EntityAttackEvent;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.core.*;
import nz.tomasborsje.duskfall.entities.MmoEntity;
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
        if(event.getTarget() instanceof MmoEntity victim
                && event.getEntity() instanceof MmoEntity attacker
                && !attacker.getStats().isDead()) {

            DamageInstance hurt = new DamageInstance(
                    MmoDamageCause.ENTITY_ATTACK,
                    MmoDamageType.PHYSICAL,
                    attacker,
                    attacker.getStats().getMeleeDamage());
            victim.hurt(hurt);

            DuskfallServer.logger.info("{} with level {} was struck for {}! They are {} hp",
                    victim.getMmoName(),
                    victim.getStats().getLevel(),
                    hurt.amount,
                    victim.getStats().getCurrentHealth());
        }
        return Result.SUCCESS;
    }
}
