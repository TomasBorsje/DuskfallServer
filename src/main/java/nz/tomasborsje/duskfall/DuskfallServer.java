package nz.tomasborsje.duskfall;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import nz.tomasborsje.duskfall.commands.DebugItemCommand;
import nz.tomasborsje.duskfall.commands.GiveItemCommand;
import nz.tomasborsje.duskfall.commands.SpawnEntityCommand;
import nz.tomasborsje.duskfall.commands.SpawnLootBagCommand;
import nz.tomasborsje.duskfall.core.MmoInstance;
import nz.tomasborsje.duskfall.database.DatabaseConnection;
import nz.tomasborsje.duskfall.entities.MmoPlayer;
import nz.tomasborsje.duskfall.events.*;
import nz.tomasborsje.duskfall.registry.Registries;
import nz.tomasborsje.duskfall.util.ResourcePackGen;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;


public class DuskfallServer {
    public static Logger logger = LoggerFactory.getLogger(DuskfallServer.class);
    public static DatabaseConnection dbConnection;
    public static MinecraftServer server;
    public static InstanceManager instanceManager;
    public static @NotNull GlobalEventHandler eventHandler;
    public static MmoInstance overworldInstance;


    public static void main(String[] args) {
        //Thread.setDefaultUncaughtExceptionHandler(DuskfallServer::handleUncaughtException);

        // Set MongoDB logger to error so it doesn't spam
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.ERROR);

        // Init registries
        Registries.ITEMS.loadItemDefinitions(new File("data", "items"));
        Registries.ENTITIES.loadEntityDefinitions(new File("data", "entities"));
        Registries.ENTITY_SPAWNERS.loadEntitySpawners(new File("data", "spawns"));

        // Connect to DB
        dbConnection = new DatabaseConnection(System.getenv("MMO_DATABASE_CONNECTION_STRING"));

        // Initialize the server
        server = MinecraftServer.init();

        instanceManager = MinecraftServer.getInstanceManager();
        eventHandler = MinecraftServer.getGlobalEventHandler();

        // Create custom instance
        overworldInstance = new MmoInstance("worlds/overworld", new File("data", "spawns"));
        instanceManager.registerInstance(overworldInstance);

        // Set our custom player provider
        MinecraftServer.getConnectionManager().setPlayerProvider(MmoPlayer::new);

        // Register events
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        registerEvents(globalEventHandler);

        // Register commands
        CommandManager commandManager = MinecraftServer.getCommandManager();
        registerCommands(commandManager);

        // TODO: Proxy?
        MojangAuth.init();

        // Generate resource pack TODO: Move this somewhere else.
        ResourcePackGen.GenerateResourcePack();

        // Finally, start the server
        logger.info("Starting server!");
        server.start("127.0.0.1", 44644);
        preloadChunks();
        logger.info("--------------");
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
        globalEventHandler.addListener(new ServerListPingListener());
        globalEventHandler.addListener(new PlayerInteractEntityListener());
    }

    private static void registerCommands(CommandManager commandManager) {
        commandManager.register(new SpawnEntityCommand());
        commandManager.register(new SpawnLootBagCommand());
        commandManager.register(new GiveItemCommand());
        commandManager.register(new DebugItemCommand());
    }

    /**
     * Called when an uncaught exception is encountered.
     * @param t The thread the uncaught exception is from
     * @param e The uncaught exception
     */
    public static void handleUncaughtException(Thread t, Throwable e) {
        logger.error("Uncaught exception during runtime: ", e);
        // Shut down server
        MinecraftServer.stopCleanly();
        dbConnection.disconnect();
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
