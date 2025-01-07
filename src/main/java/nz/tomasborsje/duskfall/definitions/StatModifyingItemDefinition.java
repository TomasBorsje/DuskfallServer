package nz.tomasborsje.duskfall.definitions;

import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.item.ItemStack;
import nz.tomasborsje.duskfall.core.ItemStackTags;
import nz.tomasborsje.duskfall.core.StatModifierTagKeys;

public class StatModifyingItemDefinition extends ItemDefinition {
    @SerializedName("stamina")
    private int stamina = 0;

    @SerializedName("strength")
    private int strength = 0;

    @SerializedName("intellect")
    private int intellect = 0;

    @SerializedName("focus")
    private int focus = 0;

    @SerializedName("melee_damage")
    private int meleeDamage = 0;

    @SerializedName("bow_damage")
    private int bowDamage = 0;

    @SerializedName("spell_power")
    private int spellPower = 0;

    @Override
    public ItemStack buildItemStack() {
        ItemStack stack = super.buildItemStack();

        // Add item stats as tag
        // TODO: Only put values if > 0
        CompoundBinaryTag statTag = CompoundBinaryTag.builder()
                .putInt(StatModifierTagKeys.STAMINA, stamina)
                .putInt(StatModifierTagKeys.STRENGTH, strength)
                .putInt(StatModifierTagKeys.INTELLECT, intellect)
                .putInt(StatModifierTagKeys.FOCUS, focus)
                .putInt(StatModifierTagKeys.MELEE_DAMAGE, meleeDamage)
                .putInt(StatModifierTagKeys.BOW_DAMAGE, bowDamage)
                .putInt(StatModifierTagKeys.SPELL_POWER, spellPower)
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
