package nz.tomasborsje.duskfall.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IMmoEntity {
    public @NotNull StatContainer getStats();
    public void hurt(DamageInstance damageInstance);
    public void kill(DamageInstance killingBlow);
}
