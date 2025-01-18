package nz.tomasborsje.duskfall.definitions.entities;

import com.google.common.collect.ImmutableBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.core.AiType;
import nz.tomasborsje.duskfall.core.HasId;
import org.jetbrains.annotations.NotNull;

public class EntityDefinition implements Cloneable, HasId {
    private static final ImmutableBiMap<String, Class<? extends EntityDefinition>> defToClassMap;
    static {
        defToClassMap = new ImmutableBiMap.Builder<String, Class<? extends EntityDefinition>>()
                .put("basic", EntityDefinition.class)
                .build();
    }

    @SerializedName("entity_type")
    String entityType = "zombie"; // Zombie by default

    @SerializedName("id")
    String id;

    @SerializedName("name")
    String name = "Unknown";

    @SerializedName("level")
    int level = 1;

    @SerializedName("ai_type")
    AiType aiType = AiType.PASSIVE;

    @SerializedName("roam_radius")
    int roamRadius = 10;

    public static EntityDefinition deserialize(Gson gson, JsonObject jsonObject)
    {
        JsonElement def = jsonObject.get("def");  // Use the 'def' field to get target class
        if(def == null) {
            DuskfallServer.logger.warn("Entity definition is missing a 'def' field!");
            return null;
        }
        Class<? extends EntityDefinition> entityDefClass = defToClassMap.get(def.getAsString());

        if(entityDefClass == null) {
            DuskfallServer.logger.warn("Tried to deserialize unknown entity definition type: {}", def);
            return null;
        }
        return gson.fromJson(jsonObject, entityDefClass);
    }

    @Override
    public EntityDefinition clone() {
        try {
            return (EntityDefinition) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    public String getEntityType() {
        return entityType;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public AiType getAiType() {
        return aiType;
    }

    public int getRoamRadius() {
        return roamRadius;
    }
}
