package nz.tomasborsje.duskfall.database;

import java.util.HashMap;
import java.util.Map;

/**
 * Data storage object that describes a player's data.
 */
public class PlayerData {
    public final String username;
    public final int level;
    public final Map<String, String> inventoryItems;

    public PlayerData(String username) {
        this.username = username;
        this.level = 1;
        this.inventoryItems = new HashMap<>();
    }

    public PlayerData(String username, int level, Map<String, String> inventoryItems) {
        this.username = username;
        this.level = level;
        this.inventoryItems = inventoryItems;
    }
}
