package nz.tomasborsje.duskfall.events;

import net.minestom.server.event.EventListener;
import net.minestom.server.event.instance.InstanceChunkLoadEvent;
import nz.tomasborsje.duskfall.core.MmoInstance;
import nz.tomasborsje.duskfall.definitions.entityspawners.EntitySpawnerDefinition;
import nz.tomasborsje.duskfall.registry.Registries;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class InstanceChunkLoadListener implements EventListener<InstanceChunkLoadEvent> {

    @Override
    public @NotNull Class<InstanceChunkLoadEvent> eventType() {
        return InstanceChunkLoadEvent.class;
    }

    @NotNull
    @Override
    public Result run(@NotNull InstanceChunkLoadEvent event) {
        if(!(event.getInstance() instanceof MmoInstance mmoInstance)) {
            return Result.SUCCESS;
        }

        // Add all entity spawners
        Collection<EntitySpawnerDefinition> spawnersForChunk = Registries.ENTITY_SPAWNERS.getSpawnersForChunk(event.getChunkX(), event.getChunkZ());
        for(EntitySpawnerDefinition spawner : spawnersForChunk) {
            mmoInstance.addEntitySpawner(spawner.buildEntitySpawner());
        }

        return Result.SUCCESS;
    }
}