package nz.tomasborsje.duskfall.core;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MmoEntity {
    public @NotNull StatContainer getStats();
    public @NotNull List<StatModifier> getStatModifiers();
    public void hurt(DamageInstance damageInstance);
    public void kill(DamageInstance killingBlow);
}
