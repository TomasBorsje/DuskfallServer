package nz.tomasborsje.duskfall.definitions.items;

import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import nz.tomasborsje.duskfall.core.ItemStackTags;
import nz.tomasborsje.duskfall.core.StatModifierTagKeys;
import nz.tomasborsje.duskfall.core.TooltipLine;
import nz.tomasborsje.duskfall.core.TooltipPosition;
import nz.tomasborsje.duskfall.util.MmoStyles;

import java.util.List;

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

    @Override
    protected void addTooltipLines(List<TooltipLine> tooltipLines) {
        super.addTooltipLines(tooltipLines);
        // Stat mods
        boolean statModSpace = false;
        if (stamina != 0) {
            statModSpace = true;
            tooltipLines.add(new TooltipLine(TooltipPosition.STAMINA_MOD, Component.text("+" + stamina + " Stamina", MmoStyles.STAT_MOD_STYLE)));
        }
        if (strength != 0) {
            statModSpace = true;
            tooltipLines.add(new TooltipLine(TooltipPosition.STRENGTH_MOD, Component.text("+" + strength + " Strength", MmoStyles.STAT_MOD_STYLE)));
        }
        if (intellect != 0) {
            statModSpace = true;
            tooltipLines.add(new TooltipLine(TooltipPosition.INTELLECT_MOD, Component.text("+" + intellect + " Intellect", MmoStyles.STAT_MOD_STYLE)));
        }
        if (focus != 0) {
            statModSpace = true;
            tooltipLines.add(new TooltipLine(TooltipPosition.AGILITY_MOD, Component.text("+" + focus + " Focus", MmoStyles.STAT_MOD_STYLE)));
        }
        if(statModSpace) {
            tooltipLines.add(new TooltipLine(TooltipPosition.STAT_MOD_SPACE, Component.text("", MmoStyles.STAT_MOD_STYLE)));
        }

        // Damage buff effects
        boolean simpleEffectSpace = false;
        if (meleeDamage != 0) {
            simpleEffectSpace = true;
            tooltipLines.add(new TooltipLine(TooltipPosition.SIMPLE_EFFECTS, Component.text("Equip: Gain " + meleeDamage + " attack damage.", MmoStyles.STAT_MOD_STYLE)));
        }
        if (bowDamage != 0) {
            simpleEffectSpace = true;
            tooltipLines.add(new TooltipLine(TooltipPosition.SIMPLE_EFFECTS, Component.text("Equip: Gain " + bowDamage + " ranged damage.", MmoStyles.STAT_MOD_STYLE)));
        }
        if (spellPower != 0) {
            simpleEffectSpace = true;
            tooltipLines.add(new TooltipLine(TooltipPosition.SIMPLE_EFFECTS, Component.text("Equip: Gain " + spellPower + " spell damage.", MmoStyles.STAT_MOD_STYLE)));
        }
        if(simpleEffectSpace) {
            tooltipLines.add(new TooltipLine(TooltipPosition.EFFECT_SPACE, Component.text("", MmoStyles.STAT_MOD_STYLE)));
        }
    }
}
