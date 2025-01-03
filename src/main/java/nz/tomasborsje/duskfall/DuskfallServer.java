package nz.tomasborsje.duskfall;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import nz.tomasborsje.duskfall.commands.SpawnEntityCommand;
import nz.tomasborsje.duskfall.core.MmoPlayer;
import nz.tomasborsje.duskfall.events.EntityMeleeAttackListener;
import nz.tomasborsje.duskfall.events.PlayerJoinListener;
import nz.tomasborsje.duskfall.events.PlayerLeaveListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;


public class DuskfallServer {
    public static Logger logger = LoggerFactory.getLogger(DuskfallServer.class);
    public static MinecraftServer server;
    public static InstanceManager instanceManager;
    public static InstanceContainer overworldInstance;
    public static void main(String[] args) {
        // Initialize the server
        server = MinecraftServer.init();

        instanceManager = MinecraftServer.getInstanceManager();
        overworldInstance = instanceManager.createInstanceContainer();

        // TODO: Configure the Overworld instance
        // Set the ChunkGenerator
        overworldInstance.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));
        // Set the world loader
        overworldInstance.setChunkSupplier(LightingChunk::new);
        overworldInstance.setChunkLoader(new AnvilLoader("worlds/overworld"));

        MinecraftServer.getConnectionManager().setPlayerProvider(MmoPlayer::new);

        // TODO: Register events here
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        registerEvents(globalEventHandler);

        CommandManager commandManager = MinecraftServer.getCommandManager();
        registerCommands(commandManager);

        MojangAuth.init();

        // Finally, start the server
        logger.info("Starting server!");
        server.start("0.0.0.0", 25565);
        preloadChunks();
        logger.info("--------------------------------------");
    }

    private static void preloadChunks() {
        var chunks = new ArrayList<CompletableFuture<Chunk>>();
        forChunksInRange(0, 0, 32, (x, z) -> chunks.add(overworldInstance.loadChunk(x, z)));

        CompletableFuture.runAsync(() -> {
            CompletableFuture.allOf(chunks.toArray(CompletableFuture[]::new)).join();
            logger.info("Chunks loaded, calculating lighting data...");
            LightingChunk.relight(overworldInstance, overworldInstance.getChunks());
            logger.info("Chunk lighting data calculated.");
            logger.info("Preloaded chunks successfully.");
        });
    }

    private static void registerEvents(GlobalEventHandler globalEventHandler) {
        globalEventHandler.addListener(new PlayerJoinListener());
        //globalEventHandler.addListener(new BlockBreakListener());
        globalEventHandler.addListener(new PlayerLeaveListener());
        globalEventHandler.addListener(new EntityMeleeAttackListener());
    }

    private static void registerCommands(CommandManager commandManager) {
        commandManager.register(new SpawnEntityCommand());
    }

    @FunctionalInterface
    public interface IntegerBiConsumer extends BiConsumer<Integer, Integer> {
        @Override
        void accept(Integer t, Integer u);
    }

    // Adapted from https://github.com/Minestom/Minestom/blob/101211c80420a59ede8a3399a287b20d58dd593e/src/main/java/net/minestom/server/utils/chunk/ChunkUtils.java
    public static void forChunksInRange(int chunkX, int chunkZ, int range, IntegerBiConsumer consumer) {
        consumer.accept(chunkX, chunkZ);
        for (int id = 1; id < (range * 2 + 1) * (range * 2 + 1); id++) {
            var index = id - 1;
            var radius = (int) Math.floor((Math.sqrt(index + 1.0) - 1) / 2) + 1;
            var p = 8 * radius * (radius - 1) / 2;
            var en = radius * 2;
            var a = (1 + index - p) % (radius * 8);
            switch (a / (radius * 2)) {
                case 0 -> consumer.accept(a - radius + chunkX, -radius + chunkZ);
                case 1 -> consumer.accept(radius + chunkX, a % en - radius + chunkZ);
                case 2 -> consumer.accept(radius - a % en + chunkX, radius + chunkZ);
                case 3 -> consumer.accept(-radius + chunkX, radius - a % en + chunkZ);
                default -> throw new IllegalStateException("unreachable");
            }
        }
    }
}
