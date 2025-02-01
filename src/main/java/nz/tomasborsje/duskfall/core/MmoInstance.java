package nz.tomasborsje.duskfall.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.world.DimensionType;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.definitions.entityspawners.EntitySpawnerDefinition;
import nz.tomasborsje.duskfall.entities.EntitySpawner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MmoInstance extends InstanceContainer {
    private final Set<EntitySpawner> entitySpawners = new HashSet<>();

    public MmoInstance(String worldFolderPath, File spawnerDefFolder) {
        // This is the default constructor called when you call InstanceManager.createInstanceContainer()
        super(MinecraftServer.getDimensionTypeRegistry(), UUID.randomUUID(), DimensionType.OVERWORLD, null, DimensionType.OVERWORLD.namespace());

        // Set defaults
        setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));
        setChunkSupplier(LightingChunk::new);
        setChunkLoader(new AnvilLoader(worldFolderPath));
    }

    public void addEntitySpawner(EntitySpawner spawner) {
        entitySpawners.add(spawner);
    }

    @Override
    public void tick(long time) {
        super.tick(time);

        // Tick entity spawners
        entitySpawners.forEach(EntitySpawner::tick);
    }
}
