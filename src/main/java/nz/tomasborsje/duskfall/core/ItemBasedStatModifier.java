package nz.tomasborsje.duskfall.core;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemBasedStatModifier implements StatModifier {
    private final int stamina;
    private final int strength;
    private final int intellect;
    private final int focus;
    private final int meleeDamageBonus;
    private final int bowDamageBonus;
    private final int spellPowerBonus;

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
            focus = 0;
            meleeDamageBonus = 0;
            bowDamageBonus = 0;
            spellPowerBonus = 0;
        }
        else {
            // Extract item stat mods
            stamina = cbt.getInt(StatModifierTagKeys.STAMINA);
            strength = cbt.getInt(StatModifierTagKeys.STRENGTH);
            intellect = cbt.getInt(StatModifierTagKeys.INTELLECT);
            focus = cbt.getInt(StatModifierTagKeys.FOCUS);
            meleeDamageBonus = cbt.getInt(StatModifierTagKeys.MELEE_DAMAGE);
            bowDamageBonus = cbt.getInt(StatModifierTagKeys.BOW_DAMAGE);
            spellPowerBonus = cbt.getInt(StatModifierTagKeys.SPELL_POWER);
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

    @Override
    public int getFocusMod() {
        return focus;
    }

    @Override
    public int getMeleeDamageBonus() {
        return meleeDamageBonus;
    }

    @Override
    public int getBowDamageBonus() {
        return bowDamageBonus;
    }

    @Override
    public int getSpellPowerBonus() {
        return spellPowerBonus;
    }
}
