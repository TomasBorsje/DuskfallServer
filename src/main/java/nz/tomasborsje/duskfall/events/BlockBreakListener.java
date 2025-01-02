package nz.tomasborsje.duskfall.events;

import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class BlockBreakListener implements EventListener<PlayerBlockBreakEvent> {

    @Override
    public @NotNull Class<PlayerBlockBreakEvent> eventType() {
        return PlayerBlockBreakEvent.class;
    }

    @NotNull
    @Override
    public Result run(@NotNull PlayerBlockBreakEvent event) {
        event.setCancelled(true);
        return Result.SUCCESS;
    }
}
