package nz.tomasborsje.duskfall.definitions;

import com.google.common.collect.ImmutableBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.core.ItemRarity;
import nz.tomasborsje.duskfall.core.ItemStackTags;
import nz.tomasborsje.duskfall.core.TooltipLine;
import nz.tomasborsje.duskfall.core.TooltipPosition;
import nz.tomasborsje.duskfall.util.MmoStyles;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the immutable definition of an item.
 */
public class ItemDefinition implements Cloneable {
    private static final Gson gson = new Gson();
    private static final ImmutableBiMap<String, Class<? extends ItemDefinition>> defToClassMap;
    static {
        defToClassMap = new ImmutableBiMap.Builder<String, Class<? extends ItemDefinition>>()
                .put("item", ItemDefinition.class)
                .put("buff", StatModifyingItemDefinition.class)
                .put("letter", LetterItemDefinition.class)
                .build();
    }

    /**
     * The ID of the item.
     */
    @SerializedName("id")
    private String id = null;

    @SerializedName("name")
    private String name = null;

    @SerializedName("description")
    private String description = "";

    @SerializedName("material")
    private String material = "stick";

    @SerializedName("rarity")
    private ItemRarity rarity = ItemRarity.COMMON;

    /**
     * Builds a persistent ItemStack populated by this item definition.
     * This ItemStack will contain an NBT tab under the 'MMO_DATA' tag that contains the stats this item provides,
     * any effects it has, etc.
     * @return A persistent ItemStack populated by this item definition.
     */
    public ItemStack buildItemStack() {
        Material stackMaterial = Material.fromNamespaceId(new NamespaceID("minecraft", material));
        if(stackMaterial == null || stackMaterial == Material.AIR) {
            DuskfallServer.logger.error("No material found with ID {}!", material);
            return ItemStack.AIR;
        }

        DuskfallServer.logger.info("Creating stack for def {}", this);

        // Get tooltip components
        List<TooltipLine> tooltipItems = new ArrayList<>();
        addTooltipLines(tooltipItems);
        List<Component> tooltipLines = tooltipItems.stream().sorted().map(TooltipLine::getComponent).toList();

        return ItemStack.builder(stackMaterial)
                .glowing()
                .customName(Component.text(name, rarity.nameStyle))
                .lore(tooltipLines)
                .set(ItemStackTags.MMO_CORE_DATA, CompoundBinaryTag.builder().putString("id", id).build())
                .set(ItemComponent.CUSTOM_MODEL_DATA, getId().toLowerCase().hashCode() % 1_000_000)
                .build();
    }

    /**
     * Adds any custom lines for this item to a set of tooltip lines.
     * @param tooltipLines The set of tooltip lines to add this item's tooltip lines to.
     */
    protected void addTooltipLines(List<TooltipLine> tooltipLines) {
        if(!description.isEmpty()) {
            tooltipLines.add(new TooltipLine(TooltipPosition.DESCRIPTION_SPACE, Component.text("", MmoStyles.DESCRIPTION)));
            tooltipLines.add(new TooltipLine(TooltipPosition.DESCRIPTION, Component.text(description, MmoStyles.DESCRIPTION)));
        }
    }

    public String getId() {
        return id;
    }

    public String getMaterial() {
        return material;
    }

    public static @Nullable ItemDefinition deserialize(JsonObject jsonObject) {
        String def = jsonObject.get("def").getAsString();  // Use the 'def' field to get target class
        Class<? extends ItemDefinition> itemDefClass = defToClassMap.get(def);

        if(itemDefClass == null) {
            DuskfallServer.logger.warn("Tried to deserialize unknown item definition type: {}", def);
            return null;
        };
        return gson.fromJson(jsonObject, itemDefClass);
    }

    public String serialize() {
        JsonObject json = gson.toJsonTree(this).getAsJsonObject();
        String def = defToClassMap.inverse().get(this.getClass());
        if(def == null) {
            throw new IllegalArgumentException("Tried to serialize unspecified ItemDefinition class "+this.getClass().getSimpleName());
        }

        json.addProperty("def", def);
        return json.toString();
    }

    @Override
    public String toString() {
        return "ItemDefinition{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", material='" + material + '\'' +
                ", rarity=" + rarity +
                '}';
    }

    /**
     * Clone this ItemDefinition to return a mutable copy.
     * Subclasses should override this for any fields they need.
     * @return A mutable copy of this item definition.
     */
    @Override
    public ItemDefinition clone() {
        try {
            return (ItemDefinition) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
