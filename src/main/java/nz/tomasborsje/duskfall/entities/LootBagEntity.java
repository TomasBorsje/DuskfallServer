package nz.tomasborsje.duskfall.entities;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.event.EventListener;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import nz.tomasborsje.duskfall.core.InteractableEntity;
import nz.tomasborsje.duskfall.core.ItemGainReason;

public class LootBagEntity extends Entity implements InteractableEntity {

    private final Entity displayEntity;
    private final ItemStack[] itemStacks;

    public LootBagEntity(Instance spawnInstance, Pos spawnPos, ItemStack... itemStacks) {
        super(EntityType.INTERACTION);
        this.itemStacks = itemStacks;

        InteractionMeta meta = ((InteractionMeta)entityMeta);
        meta.setNotifyAboutChanges(false);
        meta.setWidth(0.5f);
        meta.setHeight(0.5f);
        meta.setResponse(true);
        meta.setNotifyAboutChanges(true);

        // Spawn display block entity as well
        displayEntity = new Entity(EntityType.BLOCK_DISPLAY);
        BlockDisplayMeta displayMeta = (BlockDisplayMeta) displayEntity.getEntityMeta();
        displayMeta.setBlockState(Block.AMETHYST_BLOCK);
        displayMeta.setLeftRotation(new float[] {0, 0, 0, 1});
        displayMeta.setRightRotation(new float[] {0, 0, 0, 1});
        displayEntity.setNoGravity(true);
        displayEntity.setInstance(spawnInstance, spawnPos);
    }

    @Override
    public void tick(long time) {
        super.tick(time);
        displayEntity.teleport(this.getPosition());
    }

    @Override
    public EventListener.Result onPlayerInteract(MmoPlayer player) {
        player.playSound(Sound.sound().type(Key.key("item.bundle.drop_contents")).build());

        for(ItemStack stack : itemStacks) {
            player.giveItem(stack, ItemGainReason.LOOT);
        }

        displayEntity.remove();
        this.remove();
        return EventListener.Result.SUCCESS;
    }
}
