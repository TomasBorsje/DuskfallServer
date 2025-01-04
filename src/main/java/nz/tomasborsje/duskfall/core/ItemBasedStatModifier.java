package nz.tomasborsje.duskfall.core;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemBasedStatModifier implements StatModifier {
    private final int stamina;
    private final int strength;
    private final int intellect;

    /**
     * Calculates a stat modifier based on an ItemStack's MMO_DATA tag.
     * @param stack The stack to scan for stat modifiers.
     */
    public ItemBasedStatModifier(@NotNull ItemStack stack) {
        // Get NBT tag
        CompoundBinaryTag cbt = (CompoundBinaryTag) stack.getTag(ItemStackTags.MMO_STAT_MODS);

        if(cbt == null) {
            stamina = 0;
            strength = 0;
            intellect = 0;
        }
        else {
            // Extract item stat mods
            stamina = cbt.getInt("stamina");
            strength = cbt.getInt("strength");
            intellect = cbt.getInt("intellect");
        }
    }

    @Override
    public int getStaminaMod() {
        return stamina;
    }

    @Override
    public int getStrengthMod() {
        return strength;
    }

    @Override
    public int getIntellectMod() {
        return intellect;
    }
}
