package nz.tomasborsje.duskfall.core;

public class BasicStatModifier implements StatModifier {
    private final int staminaMod;
    private final int strengthMod;
    private final int intellectMod;

    public BasicStatModifier(int staminaMod, int strengthMod, int intellectMod) {
        this.staminaMod = staminaMod;
        this.strengthMod = strengthMod;
        this.intellectMod = intellectMod;
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
}
