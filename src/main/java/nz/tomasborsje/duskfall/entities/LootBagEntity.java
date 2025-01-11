package nz.tomasborsje.duskfall.entities;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.event.EventListener;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import nz.tomasborsje.duskfall.core.InteractableEntity;
import nz.tomasborsje.duskfall.core.ItemGainReason;
import nz.tomasborsje.duskfall.sounds.Sounds;

public class LootBagEntity extends Entity implements InteractableEntity {
    private final static float SCALE = 0.75f;
    private final static Vec SCALE_VEC = new Vec(SCALE, SCALE, SCALE);
    private final Entity displayEntity;
    private final ItemStack[] itemStacks;

    public LootBagEntity(MmoPlayer player, Pos spawnPos, ItemStack... itemStacks) {
        super(EntityType.INTERACTION);
        this.itemStacks = itemStacks;

        setNoGravity(true);
        InteractionMeta meta = ((InteractionMeta)entityMeta);
        meta.setNotifyAboutChanges(false);
        meta.setWidth(SCALE);
        meta.setHeight(SCALE);
        meta.setResponse(true);
        meta.setNotifyAboutChanges(true);

        viewers.clear();

        // Spawn display block entity as well
        displayEntity = new Entity(EntityType.BLOCK_DISPLAY);
        displayEntity.setNoGravity(true);

        BlockDisplayMeta displayMeta = (BlockDisplayMeta) displayEntity.getEntityMeta();
        displayMeta.setBlockState(Block.RAW_GOLD_BLOCK);
        displayMeta.setScale(SCALE_VEC);

        displayEntity.setInstance(player.getInstance(), spawnPos).thenRun(() -> {
            displayEntity.teleport(displayEntity.getPosition().add(-SCALE * 0.5f, 0, SCALE * 0.5f));
            displayEntity.lookAt(displayEntity.getPosition().add(1, 0, 0));
        });
    }

    @Override
    public EventListener.Result onPlayerInteract(MmoPlayer player) {
        player.playSound(Sounds.DING);

        for(ItemStack stack : itemStacks) {
            player.giveItem(stack, ItemGainReason.LOOT);
        }

        displayEntity.remove();
        this.remove();
        return EventListener.Result.SUCCESS;
    }
}
