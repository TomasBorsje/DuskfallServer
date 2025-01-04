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
        if(event.getTarget() instanceof MmoEntity entity) {
            StatContainer stats = entity.getStats();

            entity.hurt(new DamageInstance(MmoDamageCause.ENTITY_ATTACK, MmoDamageType.PHYSICAL, (MmoEntity) event.getEntity(), 7));

            DuskfallServer.logger.info(event.getTarget().getClass().getSimpleName()+" with level "+stats.getLevel()+" was struck! They are "+stats.getCurrentHealth()+" hp");
            //entity.sendActionBar(Component.text("HP: "+stats.getCurrentHealth() + "/" + stats.getMaxHealth()));
            //entity.setHealth(stats.getCurrentHealth() / (float)stats.getMaxHealth() * 20 + 0.3f);
        }

        return Result.SUCCESS;
    }

}
