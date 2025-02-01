package nz.tomasborsje.duskfall.entities;

import net.minestom.server.coordinate.Pos;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.definitions.entities.EntityDefinition;

public class EntitySpawner {
    private final String id;
    private final EntityDefinition entityDefinition;
    private final Pos spawn;
    private final int respawnTimeInTicks;
    private MmoCreature entity;
    private int ticksUntilRespawn = 0;

    public EntitySpawner(String id, EntityDefinition entityDefinition, Pos spawn, int respawnTimeInTicks) {
        this.id = id;
        this.entityDefinition = entityDefinition;
        this.spawn = spawn;
        this.respawnTimeInTicks = respawnTimeInTicks;
        this.ticksUntilRespawn = respawnTimeInTicks;
    }

    public void tick() {
        if(entity == null) {
            spawnEntity();
            return;
        }

        // If entity is dead, decrease respawn timer or spawn entity
        if(entity.getStats().isDead()) {
            if(ticksUntilRespawn > 0) {
                ticksUntilRespawn--;
            }
            else {
                spawnEntity();
            }
        }
    }

    private void spawnEntity() {
        ticksUntilRespawn = respawnTimeInTicks;
        entity = new MmoCreature(entityDefinition, spawn);
        entity.setInstance(DuskfallServer.overworldInstance);
    }
}
