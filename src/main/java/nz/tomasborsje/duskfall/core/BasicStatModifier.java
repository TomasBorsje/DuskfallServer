package nz.tomasborsje.duskfall.core;

public class BasicStatModifier implements StatModifier {
    private final int staminaMod;
    private final int strengthMod;
    private final int intellectMod;
    private final int focusMod;
    private final int meleeDamageBonus;
    private final int bowDamageBonus;
    private final int spellPowerBonus;

    // TODO: Builder pattern?
    public BasicStatModifier(int staminaMod, int strengthMod, int intellectMod, int focusMod, int meleeDamageBonus, int bowDamageBonus, int spellPowerBonus) {
        this.staminaMod = staminaMod;
        this.strengthMod = strengthMod;
        this.intellectMod = intellectMod;
        this.focusMod = focusMod;
        this.meleeDamageBonus = meleeDamageBonus;
        this.bowDamageBonus = bowDamageBonus;
        this.spellPowerBonus = spellPowerBonus;
    }

    @Override
    public int getStaminaMod() {
        return staminaMod;
    }

    @Override
    public int getStrengthMod() {
        return strengthMod;
    }

    @Override
    public int getIntellectMod() {
        return intellectMod;
    }

    @Override
    public int getFocusMod() {
        return focusMod;
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
