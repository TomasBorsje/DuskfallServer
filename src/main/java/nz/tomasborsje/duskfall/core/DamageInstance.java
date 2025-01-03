package nz.tomasborsje.duskfall.core;

import org.jetbrains.annotations.Nullable;

public class DamageInstance {
    public final MmoDamageCause cause;
    public final MmoDamageType type;
    public final IMmoEntity owner;
    public final int amount;

    public DamageInstance(MmoDamageCause cause, MmoDamageType type, @Nullable IMmoEntity owner, int amount) {
        this.cause = cause;
        this.type = type;
        this.owner = owner;
        this.amount = amount;
    }
}
