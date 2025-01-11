package nz.tomasborsje.duskfall.entities;

import net.minestom.server.entity.Entity;
import nz.tomasborsje.duskfall.buffs.Buff;
import nz.tomasborsje.duskfall.core.DamageInstance;
import nz.tomasborsje.duskfall.core.StatContainer;
import nz.tomasborsje.duskfall.core.StatModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MmoEntity {
    public @NotNull StatContainer getStats();
    public @NotNull List<StatModifier> getStatModifiers();
    public void hurt(DamageInstance damageInstance);
    public void kill(DamageInstance killingBlow);
    public void addBuff(@NotNull Buff newBuff);
    public Entity asEntity();
}
