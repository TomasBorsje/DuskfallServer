package nz.tomasborsje.duskfall.definitions;

import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.item.ItemStack;
import nz.tomasborsje.duskfall.core.ItemStackTags;

public class StatModifyingItemDefinition extends ItemDefinition {
    @SerializedName("stamina")
    private int stamina = 0;

    @SerializedName("strength")
    private int strength = 0;

    @SerializedName("intellect")
    private int intellect = 0;

    @Override
    public ItemStack buildItemStack() {
        ItemStack stack = super.buildItemStack();

        // Add item stats as tag
        CompoundBinaryTag statTag = CompoundBinaryTag.builder()
                .putInt("stamina", stamina)
                .putInt("strength", strength)
                .putInt("intellect", intellect)
                .build();
        return stack.withTag(ItemStackTags.MMO_STAT_MODS, statTag);
    }

    public int getStamina() {
        return stamina;
    }

    public int getStrength() {
        return strength;
    }

    public int getIntellect() {
        return intellect;
    }
}
