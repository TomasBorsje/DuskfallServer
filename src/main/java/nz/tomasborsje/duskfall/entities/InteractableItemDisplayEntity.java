package nz.tomasborsje.duskfall.entities;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.item.ItemStack;
import nz.tomasborsje.duskfall.core.InteractableEntity;

/**
 * Represents an interactable entity with an ItemDisplayEntity that displays a model in game.
 */
public abstract class InteractableItemDisplayEntity extends Entity implements InteractableEntity {
    protected final Entity itemDisplayEntity;
    private final float width;
    private final float height;

    public InteractableItemDisplayEntity(float interactionWidth, float interactionHeight, float modelScale, ItemStack displayStack) {
        super(EntityType.INTERACTION);
        this.width = interactionWidth;
        this.height = interactionHeight;

        // TODO: Re-enable gravity on both entities once the hitboxes line up - may need to set translation on display
        setNoGravity(true);

        InteractionMeta meta = ((InteractionMeta)entityMeta);
        meta.setNotifyAboutChanges(false);
        meta.setWidth(interactionWidth);
        meta.setHeight(interactionHeight);
        meta.setResponse(true);
        meta.setNotifyAboutChanges(true);

        // Spawn display block entity
        itemDisplayEntity = new Entity(EntityType.ITEM_DISPLAY);
        itemDisplayEntity.setNoGravity(true);

        ItemDisplayMeta displayMeta = (ItemDisplayMeta) itemDisplayEntity.getEntityMeta();
        displayMeta.setNotifyAboutChanges(false);
        displayMeta.setItemStack(displayStack);
        displayMeta.setDisplayContext(ItemDisplayMeta.DisplayContext.GROUND);
        displayMeta.setScale(new Vec(modelScale, modelScale, modelScale));
        displayMeta.setNotifyAboutChanges(true);
    }

    @Override
    public void tick(long time) {
        super.tick(time);

    }

    @Override
    public void spawn() {
        Pos pos = this.getPosition();
        // Spawn display entity and set alignment
        itemDisplayEntity.setInstance(instance, pos);
        itemDisplayEntity.lookAt(itemDisplayEntity.getPosition().add(1, 0,0));
    }
}
