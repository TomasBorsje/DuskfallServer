package nz.tomasborsje.duskfall.definitions;

import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.core.ItemRarity;

/**
 * Represents the immutable definition of an item.
 */
public class ItemDefinition {

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
            DuskfallServer.logger.error("No material found with ID "+material+"!");
            return ItemStack.AIR;
        }

        DuskfallServer.logger.info("Creating stack for def "+this);

        return ItemStack.builder(stackMaterial)
                .glowing()
                .customName(Component.text(name, rarity.nameStyle))
                .lore(Component.text(description, NamedTextColor.AQUA))
                .set(ItemComponent.CUSTOM_MODEL_DATA, getId().toLowerCase().hashCode() % 1_000_000)
                .build();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMaterial() {
        return material;
    }

    public ItemRarity getRarity() {
        return rarity;
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
}
