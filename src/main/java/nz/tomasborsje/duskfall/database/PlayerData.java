package nz.tomasborsje.duskfall.database;

/**
 * Data storage object that describes a player's data.
 */
public class PlayerData {
    public final String username;
    public final int level;

    public PlayerData(String username, int level) {
        this.username = username;
        this.level = level;
    }
}
