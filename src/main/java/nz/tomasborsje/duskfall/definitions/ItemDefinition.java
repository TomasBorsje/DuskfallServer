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
import nz.tomasborsje.duskfall.core.TooltipLine;
import nz.tomasborsje.duskfall.core.TooltipPosition;
import nz.tomasborsje.duskfall.util.MmoStyles;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

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

        DuskfallServer.logger.info("Creating stack for def {}", this);

        // Get tooltip components
        List<TooltipLine> tooltipItems = new ArrayList<>();
        addTooltipLines(tooltipItems);
        List<Component> tooltipLines = tooltipItems.stream().sorted().map(TooltipLine::component).toList();

        return ItemStack.builder(stackMaterial)
                .glowing()
                .customName(Component.text(name, rarity.nameStyle))
                .lore(tooltipLines)
                .set(ItemComponent.CUSTOM_MODEL_DATA, getId().toLowerCase().hashCode() % 1_000_000)
                .build();
    }

    /**
     * Adds any custom lines for this item to a set of tooltip lines.
     * @param tooltipLines The set of tooltip lines to add this item's tooltip lines to.
     */
    protected void addTooltipLines(List<TooltipLine> tooltipLines) {
        tooltipLines.add(new TooltipLine(TooltipPosition.ITEM_TYPE, Component.text(WordUtils.capitalize(material.replace('_', ' ')), MmoStyles.ITEM_TYPE)));
        if(!description.isEmpty()) {
            tooltipLines.add(new TooltipLine(TooltipPosition.DESCRIPTION_SPACE, Component.text("", MmoStyles.DESCRIPTION)));
            tooltipLines.add(new TooltipLine(TooltipPosition.DESCRIPTION, Component.text(description, MmoStyles.DESCRIPTION)));
        }
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
