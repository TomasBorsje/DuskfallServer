package nz.tomasborsje.duskfall.core;

import nz.tomasborsje.duskfall.entities.MmoEntity;
import org.jetbrains.annotations.Nullable;

public class DamageInstance {
    public final MmoDamageCause cause;
    public final MmoDamageType type;
    public final MmoEntity owner;
    public final int amount;

    public DamageInstance(MmoDamageCause cause, MmoDamageType type, @Nullable MmoEntity owner, int amount) {
        this.cause = cause;
        this.type = type;
        this.owner = owner;
        this.amount = amount;
    }
}
