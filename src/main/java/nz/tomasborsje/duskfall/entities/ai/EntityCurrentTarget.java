package nz.tomasborsje.duskfall.entities.ai;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.ai.TargetSelector;
import org.jetbrains.annotations.NotNull;

public class EntityCurrentTarget extends TargetSelector {

    private static final int RANGE = 20;

    public EntityCurrentTarget(@NotNull EntityCreature entityCreature) {
        super(entityCreature);
    }

    @Override
    public Entity findTarget() {
        Entity entity = entityCreature.getTarget();
        if(entity == null) {
            return null;
        }
        // Check range
        return entityCreature.getDistanceSquared(entity) < RANGE * RANGE ? entity : null;
    }
}
