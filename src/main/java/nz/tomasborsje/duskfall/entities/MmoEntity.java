package nz.tomasborsje.duskfall.entities;

import net.minestom.server.entity.Entity;
import nz.tomasborsje.duskfall.buffs.Buff;
import nz.tomasborsje.duskfall.core.DamageInstance;
import nz.tomasborsje.duskfall.core.StatContainer;
import nz.tomasborsje.duskfall.core.StatModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a living entity in the MMO world.
 */
public interface MmoEntity {
    /**
     * Get the stat container for this entity.
     * @return The stat container for this entity.
     */
    @NotNull StatContainer getStats();

    /**
     * Get the stat modifiers affecting this entity.
     * @return A list of the stat modifiers affecting this entity.
     */
    @NotNull List<StatModifier> getStatModifiers();

    /**
     * Get the name of this entity.
     * @return The name of this entity.
     */
    @NotNull String getMmoName();

    /**
     * Hurt this entity using a given damage instance.
     * @param damageInstance The damage instance to hurt this entity with.
     */
    int hurt(@NotNull DamageInstance damageInstance);

    /**
     * Kill this entity.
     * @param killingBlow The damage instance that resulted in the death of this entity.
     */
    void kill(@NotNull DamageInstance killingBlow);

    /**
     * Add a buff (or debuff) to this entity.
     * @param newBuff The buff to add to this entity.
     */
    void addBuff(@NotNull Buff newBuff);

    /**
     * Get the base Entity object of this MMO entity.
     * @return The base Entity object of this MMO entity.
     */
    Entity asEntity();
}
