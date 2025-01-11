package nz.tomasborsje.duskfall.buffs;

import nz.tomasborsje.duskfall.core.DamageInstance;
import nz.tomasborsje.duskfall.entities.MmoEntity;
import org.jetbrains.annotations.NotNull;

public abstract class Buff {

    private final String id;
    protected final MmoEntity entity;
    protected int remainingLifetime;

    public Buff(@NotNull String id, @NotNull MmoEntity entity, int durationInTicks) {
        this.id = id;
        this.entity = entity;
        remainingLifetime = durationInTicks;
    }

    public void tick() {
        remainingLifetime -= 1;
    }

    /**
     * Called when the buff is removed.
     */
    public void onRemove() { }

    /**
     * Called when the owner of this buff dies. Note that onRemove will also be called.
     */
    public void onOwnerDie(DamageInstance damageInstance) { }

    /**
     * Whether or not a buff should be replaced when the same buff is re-applied.
     */
    public abstract boolean shouldReplaceExisting();

    /**
     * Whether or not the buff should be removed, e.g. it has expired.
     * @return If the buff should be removed before the next tick.
     */
    public boolean shouldRemove() {
        return remainingLifetime <= 0;
    }

    public String getId() {
        return id;
    }
}
