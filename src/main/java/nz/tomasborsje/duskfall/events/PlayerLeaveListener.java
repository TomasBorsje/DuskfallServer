package nz.tomasborsje.duskfall.events;

import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.entities.MmoPlayer;
import nz.tomasborsje.duskfall.database.PlayerData;
import org.jetbrains.annotations.NotNull;

public class PlayerLeaveListener implements EventListener<PlayerDisconnectEvent> {
    @Override
    public @NotNull Class<PlayerDisconnectEvent> eventType() {
        return PlayerDisconnectEvent.class;
    }

    @NotNull
    @Override
    public Result run(@NotNull PlayerDisconnectEvent event) {

        if(event.getPlayer() instanceof MmoPlayer mmoPlayer) {
            PlayerData data = mmoPlayer.getPlayerData();
            DuskfallServer.dbConnection.savePlayerData(data);
            DuskfallServer.logger.info("Saved player data for player {} to DB!", data.username);
        }

        return Result.SUCCESS;
    }
}
