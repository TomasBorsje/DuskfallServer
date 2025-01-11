package nz.tomasborsje.duskfall.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.entities.MmoPlayer;
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

        // Only whitelist myself
        if(!event.getPlayer().getUsername().equals("Wingmann")) {
            DuskfallServer.logger.warn("Some moron named {} tried to join!", event.getPlayer().getUsername());
            event.getPlayer().kick(Component.text("?", NamedTextColor.RED));
        }

        if(player instanceof MmoPlayer mmoPlayer) {
            DuskfallServer.logger.info("Loaded player data for player {} who is level {}!", mmoPlayer.getUsername(), mmoPlayer.getStats().getLevel());
            player.setRespawnPoint(new Pos(0, 42, 0));
            event.setSpawningInstance(DuskfallServer.overworldInstance);
        }
        else {
            event.getPlayer().kick(Component.text("Couldn't cast Player to MmoPlayer, please report this!", NamedTextColor.RED));
        }
        return Result.SUCCESS;
    }
}
