package nz.tomasborsje.duskfall.definitions.entityspawners;

import com.google.gson.annotations.SerializedName;
import net.minestom.server.coordinate.Pos;
import nz.tomasborsje.duskfall.entities.EntitySpawner;
import nz.tomasborsje.duskfall.registry.Registries;

public class EntitySpawnerDefinition {
    @SerializedName("spawner_id")
    private String spawnerId = "";

    @SerializedName("entity_id")
    private String entityId = "";

    @SerializedName("position")
    private String position = "0, 40, 0";

    @SerializedName("respawn_time")
    private int respawnTimeInTicks = 200;

    public EntitySpawner buildEntitySpawner() {
        if(spawnerId.isBlank()) {
            throw new IllegalArgumentException("Missing or empty entity spawner definition spawner_id field!");
        }
        if(entityId.isBlank() || !Registries.ENTITIES.containsId(entityId)) {
            throw new IllegalArgumentException("Invalid entity ID "+entityId+" in entity spawner definition "+spawnerId+"!");
        }
        if(respawnTimeInTicks < 0) {
            throw new IllegalArgumentException("Invalid respawn time "+respawnTimeInTicks+" in entity spawner definition!");
        }

        try {
            String[] coordinateStrings = position.split(",");
            float x = Float.parseFloat(coordinateStrings[0]);
            float y = Float.parseFloat(coordinateStrings[1]);
            float z = Float.parseFloat(coordinateStrings[2]);
            return new EntitySpawner(spawnerId, Registries.ENTITIES.get(entityId), new Pos(x, y, z), respawnTimeInTicks);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid position ("+position+") in entity spawner definition!");
        }
    }
}
