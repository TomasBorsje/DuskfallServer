package nz.tomasborsje.duskfall.registry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.definitions.ItemDefinition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;

/**
 * Static class that stores item definitions.
 * TODO: Make this use Supplier<ItemDefinition> for safe mutable instance generation.
 */
public class ItemRegistry {
    private static final HashMap<String, ItemDefinition> itemRegistry = new HashMap<>();

    /**
     * Returns a random item definition from the registry.
     *
     * @return A random item definition.
     */
    @Deprecated
    public static ItemDefinition GetRandomItem() {
        int num = (int) (Math.random() * itemRegistry.size());
        for (ItemDefinition t : itemRegistry.values()) if (--num < 0) return t;
        throw new AssertionError();
    }

    /**
     * Gets an item definition from the registry. Note that this is a clone, so can safely be modified.
     *
     * @param id The ID of the item definition.
     * @return The item definition, null if not found.
     */
    public static ItemDefinition Get(String id) {
        // Check the item exists first
        if (!ContainsId(id)) {
            DuskfallServer.logger.warn("Attempted to get item definition with id {} that doesn't exist!", id);
            return null;
        }
        return itemRegistry.get(id).clone();
    }

    /**
     * Gets all item definitions from the registry.
     * WARNING: Expensive call, use sparingly.
     *
     * @return An unmodifiable collection of all item definitions.
     */
    public static Collection<ItemDefinition> GetAllItems() {
        return itemRegistry.values().stream().map(ItemDefinition::clone).toList();
    }

    public static boolean ContainsId(String id) {
        return itemRegistry.containsKey(id);
    }

    /**
     * Loads all .json items from the plugin's data folder.
     * TODO: Deserialize into Supplier<ItemDefinition> objects instead.
     *
     * @param itemDefFolder Folder containing item definitions
     */
    public static void LoadItemDefinitions(File itemDefFolder) {
        if (!itemDefFolder.exists()) {
            if (!itemDefFolder.mkdir()) {
                DuskfallServer.logger.warn("Failed to create /items subfolder!");
            }
        }
        DuskfallServer.logger.info("Loading item schema from {}", itemDefFolder.getAbsolutePath());

        // Get all .json files in the /items subfolder
        File[] itemFiles = itemDefFolder.listFiles((dir, name) -> name.endsWith(".json"));
        assert itemFiles != null;
        DuskfallServer.logger.info("Number of item def files: {}", itemFiles.length);

        // Load each item
        for (File itemFile : itemFiles) {
            String json = "";
            try {
                json = Files.readString(itemFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Parse an array of item definitions
            Gson gson = new Gson();  // Create a Gson instance
            JsonElement jsonElement = gson.fromJson(json, JsonElement.class);  // Convert the JSON string to JsonElement

            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                DuskfallServer.logger.info("Loading {} item definitions from {}", jsonArray.size(), itemFile.getName());

                for (JsonElement element : jsonArray) {
                    ItemDefinition itemDefinition = ItemDefinition.deserialize(element.getAsJsonObject());
                    RegisterItem(itemDefinition);
                }
            } else {
                DuskfallServer.logger.error("Invalid JSON format, expected an array of items.");
            }
        }
    }

    /**
     * Registers an item definition into the registry.
     *
     * @param itemDefinition The item definition to register.
     */
    public static void RegisterItem(ItemDefinition itemDefinition) {
        // Preconditions
        if (itemDefinition == null) {
            DuskfallServer.logger.warn("Attempted to register null item definition!");
            return;
        }
        if (itemDefinition.getId() == null || itemDefinition.getId().isEmpty()) {
            DuskfallServer.logger.warn("Attempted to register item definition with null or empty id!");
            return;
        }

        // Register the item definition
        itemRegistry.put(itemDefinition.getId(), itemDefinition);
    }
}