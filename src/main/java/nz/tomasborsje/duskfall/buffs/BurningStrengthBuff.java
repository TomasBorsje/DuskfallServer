package nz.tomasborsje.duskfall.buffs;

import nz.tomasborsje.duskfall.core.*;
import org.jetbrains.annotations.NotNull;

public class BurningStrengthBuff extends Buff implements StatModifier {

    public BurningStrengthBuff(@NotNull MmoEntity entity) {
        super("burning_strength", entity, 200);
    }

    @Override
    public void onRemove() {
        // Deal 200 damage on removal
        entity.hurt(new DamageInstance(MmoDamageCause.DEBUFF, MmoDamageType.MAGIC, null, 200));
    }

    @Override
    public boolean shouldReplaceExisting() {
        return true;
    }

    @Override
    public int getStrengthMod() {
        return 500;
    }
}
