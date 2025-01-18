package nz.tomasborsje.duskfall.entities.ai;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.ai.TargetSelector;
import nz.tomasborsje.duskfall.entities.MmoCreature;
import org.jetbrains.annotations.NotNull;

public class EntityCurrentTarget extends TargetSelector {

    private static final int RANGE = 20;

    public EntityCurrentTarget(@NotNull MmoCreature entityCreature) {
        super(entityCreature);
    }

    @Override
    public Entity findTarget() {
        Entity entity = entityCreature.getTarget();
        if(entity == null) {
            return null;
        }
        // If in range, set target and enter combat
        if(entityCreature.getDistanceSquared(entity) < RANGE * RANGE) {
            ((MmoCreature)entityCreature).setInCombat();
            return entity;
        }
        return null;
    }
}
