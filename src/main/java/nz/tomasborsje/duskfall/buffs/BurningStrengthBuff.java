package nz.tomasborsje.duskfall.buffs;

import nz.tomasborsje.duskfall.core.*;
import org.jetbrains.annotations.NotNull;

/**
 * Example buff that grants 500 strength, burns the player for 20 damage every second, and burns the player for
 * 200 damage when the buff expires. Lasts for 10 seconds.
 */
public class BurningStrengthBuff extends Buff implements StatModifier {

    public BurningStrengthBuff(@NotNull MmoEntity entity) {
        super("burning_strength", entity, 200);
    }

    @Override
    public void tick() {
        super.tick();
        // Every second
        if (remainingLifetime%20==0) {
           // Burn player for 20 damage every second
           entity.hurt(new DamageInstance(MmoDamageCause.DEBUFF, MmoDamageType.MAGIC, null, 20));
        }
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
