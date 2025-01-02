package nz.tomasborsje.duskfall.events;

import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import nz.tomasborsje.duskfall.DuskfallServer;
import org.jetbrains.annotations.NotNull;

public class PlayerLeaveListener implements EventListener<PlayerDisconnectEvent> {
    @Override
    public @NotNull Class<PlayerDisconnectEvent> eventType() {
        return PlayerDisconnectEvent.class;
    }

    @NotNull
    @Override
    public Result run(@NotNull PlayerDisconnectEvent event) {
        DuskfallServer.overworldInstance.saveChunksToStorage().join();
        DuskfallServer.logger.info("Saved chunks.");
        return Result.SUCCESS;
    }
}
