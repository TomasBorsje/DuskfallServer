package nz.tomasborsje.duskfall.core;

import net.minestom.server.entity.Player;
import net.minestom.server.network.PlayerProvider;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import nz.tomasborsje.duskfall.DuskfallServer;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class MmoPlayer extends Player implements PlayerProvider {
    public int random;
    public MmoPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);
        random = new Random().nextInt(50);
        DuskfallServer.logger.info("Player object created with random number "+random);
    }

    @Override
    public @NotNull Player createPlayer(@NotNull PlayerConnection connection, @NotNull GameProfile gameProfile) {
        return new MmoPlayer(connection, gameProfile);
    }
}
