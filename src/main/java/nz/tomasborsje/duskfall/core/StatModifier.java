package nz.tomasborsje.duskfall.core;

public interface StatModifier {
    default int getStaminaMod() { return 0; }
    default int getStrengthMod() { return 0; }
    default int getIntellectMod() { return 0; }
    default int getFocusMod() { return 0; }
    default int getMeleeDamageBonus() { return 0; }
    default int getBowDamageBonus() { return 0; }
    default int getSpellPowerBonus() { return 0; }
}
