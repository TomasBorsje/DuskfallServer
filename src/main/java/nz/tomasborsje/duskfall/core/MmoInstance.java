package nz.tomasborsje.duskfall.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.DimensionType;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.definitions.entityspawners.EntitySpawnerDefinition;
import nz.tomasborsje.duskfall.entities.EntitySpawner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MmoInstance extends InstanceContainer {
    private final Set<EntitySpawner> entitySpawners = new HashSet<>();
    private final static Gson gson = new Gson();

    public MmoInstance(String worldFolderPath, File spawnerDefFolder) {
        // This is the default constructor called when you call InstanceManager.createInstanceContainer()
        super(MinecraftServer.getDimensionTypeRegistry(), UUID.randomUUID(), DimensionType.OVERWORLD, null, DimensionType.OVERWORLD.namespace());

        // Set defaults
        setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));
        setChunkSupplier(LightingChunk::new);
        setChunkLoader(new AnvilLoader(worldFolderPath));
        loadEntitySpawners(spawnerDefFolder);
    }

    private void loadEntitySpawners(File entitySpawnerDefFolder) {
        if (!entitySpawnerDefFolder.exists()) {
            if (!entitySpawnerDefFolder.mkdir()) {
                DuskfallServer.logger.warn("Failed to create /spawns subfolder!");
            }
        }
        DuskfallServer.logger.info("Loading entity spawns from {}", entitySpawnerDefFolder.getAbsolutePath());

        // Get all .json files in the /entities subfolder
        File[] spawnerFiles = entitySpawnerDefFolder.listFiles((dir, name) -> name.endsWith(".json"));
        assert spawnerFiles != null;
        DuskfallServer.logger.info("Number of entity spawns files: {}", spawnerFiles.length);

        // Load each entity
        for (File spawnFile : spawnerFiles) {
            String json = "";
            try {
                json = Files.readString(spawnFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Parse an array of entity definitions
            JsonElement jsonElement = gson.fromJson(json, JsonElement.class);  // Convert the JSON string to JsonElement

            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();

                DuskfallServer.logger.info("Loading {} entity spawner definitions from {}", jsonArray.size(), spawnFile.getName());
                for (JsonElement element : jsonArray) {
                    EntitySpawnerDefinition spawnerDef = gson.fromJson(element, EntitySpawnerDefinition.class);
                    addEntitySpawner(spawnerDef.buildEntitySpawner());
                }
            } else {
                DuskfallServer.logger.error("Invalid JSON format in file {}, expected an array of entity spawner definitions!", spawnFile.getAbsolutePath());
            }
        }
    }

    public void addEntitySpawner(EntitySpawner spawner) {
        entitySpawners.add(spawner);
    }

    @Override
    public void tick(long time) {
        super.tick(time);

        // Tick entity spawners
        entitySpawners.forEach(EntitySpawner::tick);
    }
}
