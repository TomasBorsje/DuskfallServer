package nz.tomasborsje.duskfall.entities.ai;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.ai.TargetSelector;
import net.minestom.server.instance.Instance;
import nz.tomasborsje.duskfall.entities.MmoCreature;
import nz.tomasborsje.duskfall.entities.MmoPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * Target the closest player within range.
 */
public class NearbyPlayerTarget extends TargetSelector {
    private final double range;

    /**
     * @param entityCreature  the entity (self)
     * @param range           the maximum range the entity can target others within
     */
    public NearbyPlayerTarget(@NotNull MmoCreature entityCreature, double range) {
        super(entityCreature);
        this.range = range;
    }

    @Override
    public Entity findTarget() {
        Instance instance = entityCreature.getInstance();
        if (instance == null) {
            return null;
        }

        var entity = instance.getNearbyEntities(entityCreature.getPosition(), range).stream()
                // Don't target our self and make sure entity is valid and a player
                .filter(ent -> !entityCreature.equals(ent) && !ent.isRemoved())
                .filter(ent -> ent instanceof MmoPlayer)
                .min(Comparator.comparingDouble(e -> e.getDistanceSquared(entityCreature)));

        // If we found a target, enter combat
        if(entity.isPresent()) {
            ((MmoCreature)entityCreature).setInCombat();
            return entity.get();
        }
        return null;
    }

}
