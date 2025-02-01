package nz.tomasborsje.duskfall.registry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.definitions.entityspawners.EntitySpawnerDefinition;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * Stores entity spawner definitions per chunk.
 */
public class EntitySpawnerRegistry {
    private final static Gson gson = new Gson();
    private final HashMap<Pair<Integer, Integer>, Collection<EntitySpawnerDefinition>> spawnerDefinitions = new HashMap<>();

    public void loadEntitySpawners(File entitySpawnerDefFolder) {
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
                    addSpawnerDefinition(spawnerDef);
                }
            } else {
                DuskfallServer.logger.error("Invalid JSON format in file {}, expected an array of entity spawner definitions!", spawnFile.getAbsolutePath());
            }
        }
    }

    /**
     * Add a spawner definition to the registry.
     * @param spawnerDef The spawner definition to add.
     */
    public void addSpawnerDefinition(EntitySpawnerDefinition spawnerDef) {
        String[] positionArr = spawnerDef.getPosition().split(",");
        int posX = Integer.parseInt(positionArr[0].trim());
        int posZ = Integer.parseInt(positionArr[2].trim());
        int chunkX = posX/16;
        int chunkZ = posZ/16;

        Pair<Integer, Integer> key = new ImmutablePair<>(chunkX, chunkZ);
        if(!spawnerDefinitions.containsKey(key)) {
            // Add new list
            spawnerDefinitions.put(key, new HashSet<>());
        }

        spawnerDefinitions.get(key).add(spawnerDef);
    }

    /**
     * Get an unmodifiable list of all spawner definitions for the given chunk coordinates.
     * @param chunkX Chunk X coordinate to get spawner definitions for.
     * @param chunkZ Chunk Z coordinate to get spawner definitions for.
     * @return An unmodifiable collection of spawner definitions.
     */
    public Collection<EntitySpawnerDefinition> getSpawnersForChunk(int chunkX, int chunkZ) {
        Pair<Integer, Integer> key = new ImmutablePair<>(chunkX, chunkZ);
        if(!spawnerDefinitions.containsKey(key)) {
            return List.of();
        }
        return Collections.unmodifiableCollection(spawnerDefinitions.get(key));
    }
}
