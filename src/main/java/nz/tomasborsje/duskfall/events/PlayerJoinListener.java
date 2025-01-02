package nz.tomasborsje.duskfall.events;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import nz.tomasborsje.duskfall.DuskfallServer;
import org.jetbrains.annotations.NotNull;

/**
 * Handles player connections, directing them to the Overworld instance.
 */
public class PlayerJoinListener implements EventListener<AsyncPlayerConfigurationEvent> {
    @Override
    public @NotNull Class<AsyncPlayerConfigurationEvent> eventType() {
        return AsyncPlayerConfigurationEvent.class;
    }

    @NotNull
    @Override
    public Result run(@NotNull AsyncPlayerConfigurationEvent event) {
        final Player player = event.getPlayer();
        event.setSpawningInstance(DuskfallServer.overworldInstance);
        player.setRespawnPoint(new Pos(0, 42, 0));

        return Result.SUCCESS;
    }
}
