package nz.tomasborsje.duskfall.registry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.definitions.entities.EntityDefinition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;

public class EntityRegistry {
    private final static Gson gson = new Gson();
    private final HashMap<String, EntityDefinition> entityRegistry = new HashMap<>();

    /**
     * Returns a random entity definition from the registry.
     *
     * @return A random entity definition.
     */
    @Deprecated
    public EntityDefinition getRandomEntity() {
        int num = (int) (Math.random() * entityRegistry.size());
        for (EntityDefinition t : entityRegistry.values()) if (--num < 0) return t;
        throw new AssertionError();
    }

    /**
     * Gets an entity definition from the registry. Note that this is a clone, so can safely be modified.
     *
     * @param id The ID of the entity definition.
     * @return The entity definition, null if not found.
     */
    public EntityDefinition get(String id) {
        // Check the entity exists first
        if (!containsId(id)) {
            DuskfallServer.logger.warn("Attempted to get entity definition with id {} that doesn't exist!", id);
            return null;
        }
        return entityRegistry.get(id).clone();
    }

    /**
     * Gets all entity definitions from the registry.
     * WARNING: Expensive call, use sparingly.
     *
     * @return An unmodifiable collection of all entity definitions.
     */
    public Collection<EntityDefinition> getAllEntities() {
        return entityRegistry.values().stream().map(EntityDefinition::clone).toList();
    }

    public boolean containsId(String id) {
        return entityRegistry.containsKey(id);
    }
    
    /**
     * Loads all .json entities from the plugin's data folder.
     * TODO: Deserialize into Supplier<EntityDefinition> objects instead.
     *
     * @param entityDefFolder Folder containing entity definitions
     */
    public void loadEntityDefinitions(File entityDefFolder) {
        if (!entityDefFolder.exists()) {
            if (!entityDefFolder.mkdir()) {
                DuskfallServer.logger.warn("Failed to create /entities subfolder!");
            }
        }
        DuskfallServer.logger.info("Loading entity definitions from {}", entityDefFolder.getAbsolutePath());

        // Get all .json files in the /entitys subfolder
        File[] entityFiles = entityDefFolder.listFiles((dir, name) -> name.endsWith(".json"));
        assert entityFiles != null;
        DuskfallServer.logger.info("Number of entity def files: {}", entityFiles.length);

        // Load each entity
        for (File entityFile : entityFiles) {
            String json = "";
            try {
                json = Files.readString(entityFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Parse an array of entity definitions
            JsonElement jsonElement = gson.fromJson(json, JsonElement.class);  // Convert the JSON string to JsonElement

            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                DuskfallServer.logger.info("Loading {} entity definitions from {}", jsonArray.size(), entityFile.getName());

                for (JsonElement element : jsonArray) {
                    EntityDefinition entityDef = EntityDefinition.deserialize(gson, element.getAsJsonObject());
                    registerEntity(entityDef);
                }
            } else {
                DuskfallServer.logger.error("Invalid JSON format in file {}, expected an array of entity definitions!", entityFile.getAbsolutePath());
            }
        }
    }

    /**
     * Registers an entity definition into the registry.
     *
     * @param entityDefinition The entity definition to register.
     */
    public void registerEntity(EntityDefinition entityDefinition) {
        // Preconditions
        if (entityDefinition == null) {
            DuskfallServer.logger.warn("Attempted to register null entity definition!");
            return;
        }
        if (entityDefinition.getId() == null || entityDefinition.getId().isEmpty()) {
            DuskfallServer.logger.warn("Attempted to register entity definition with null or empty id!");
            return;
        }

        entityRegistry.put(entityDefinition.getId(), entityDefinition);
    }
}
