package nz.tomasborsje.duskfall.events;

import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.core.*;
import nz.tomasborsje.duskfall.entities.MmoPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player does a left click on an entity or with net.minestom.server.entity.EntityCreature.attack(Entity).
 */
public class PlayerInteractEntityListener implements EventListener<PlayerEntityInteractEvent> {
    @Override
    public @NotNull Class<PlayerEntityInteractEvent> eventType() {
        return PlayerEntityInteractEvent.class;
    }

    @NotNull
    @Override
    public Result run(@NotNull PlayerEntityInteractEvent event) {
        DuskfallServer.logger.info("Player clicked a {}!", event.getTarget().getClass().getSimpleName());

        // Check if entity implements InteractableEntity
        if(event.getTarget() instanceof InteractableEntity interactableEntity && event.getEntity() instanceof MmoPlayer mmoPlayer) {
            return interactableEntity.onPlayerInteract(mmoPlayer);
        }

        return Result.SUCCESS;
    }
}
