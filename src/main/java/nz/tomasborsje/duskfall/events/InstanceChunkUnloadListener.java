package nz.tomasborsje.duskfall.events;

import net.minestom.server.event.EventListener;
import net.minestom.server.event.instance.InstanceChunkUnloadEvent;
import nz.tomasborsje.duskfall.core.MmoInstance;
import nz.tomasborsje.duskfall.definitions.entityspawners.EntitySpawnerDefinition;
import nz.tomasborsje.duskfall.registry.Registries;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class InstanceChunkUnloadListener implements EventListener<InstanceChunkUnloadEvent> {

    @Override
    public @NotNull Class<InstanceChunkUnloadEvent> eventType() {
        return InstanceChunkUnloadEvent.class;
    }

    @NotNull
    @Override
    public Result run(@NotNull InstanceChunkUnloadEvent event) {
        if(!(event.getInstance() instanceof MmoInstance mmoInstance)) {
            return Result.SUCCESS;
        }

        // Remove all entity spawners for this chunk
        Collection<EntitySpawnerDefinition> spawnersForChunk = Registries.ENTITY_SPAWNERS.getSpawnersForChunk(event.getChunkX(), event.getChunkZ());
        for(EntitySpawnerDefinition spawner : spawnersForChunk) {
            mmoInstance.addEntitySpawner(spawner.buildEntitySpawner());
        }
        
        return Result.SUCCESS;
    }
}