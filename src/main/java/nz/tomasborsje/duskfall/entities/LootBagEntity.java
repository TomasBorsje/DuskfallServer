package nz.tomasborsje.duskfall.entities;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.other.InteractionMeta;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryClickEvent;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.core.InteractableEntity;
import nz.tomasborsje.duskfall.core.ItemGainReason;
import nz.tomasborsje.duskfall.sounds.Sounds;

import java.util.Random;

/**
 * Loot bag entities can be interacted with by players to open a loot screen, allowing players to loot
 * items. Commonly dropped by mobs.
 */
public class LootBagEntity extends Entity implements InteractableEntity {
    private final static float SCALE = 0.75f;
    private final static Vec SCALE_VEC = new Vec(SCALE, SCALE, SCALE);
    private final static Random rand = new Random();
    private final Entity displayEntity;
    private final Inventory lootScreen;
    private final EventNode<InventoryEvent> lootScreenEventHandler;

    public LootBagEntity(MmoPlayer player, Pos spawnPos, Component title, ItemStack... itemStacks) {
        super(EntityType.INTERACTION);


        InteractionMeta meta = ((InteractionMeta)entityMeta);
        meta.setNotifyAboutChanges(false);
        meta.setWidth(SCALE);
        meta.setHeight(SCALE);
        meta.setResponse(true);
        meta.setNotifyAboutChanges(true);

        viewers.clear();

        // Spawn display block entity
        displayEntity = new Entity(EntityType.BLOCK_DISPLAY);

        // TODO: Re-enable gravity on both entities once the hitboxes line up - may need to set translation on display
        setNoGravity(true);
        displayEntity.setNoGravity(true);

        BlockDisplayMeta displayMeta = (BlockDisplayMeta) displayEntity.getEntityMeta();
        displayMeta.setBlockState(Block.RAW_GOLD_BLOCK);
        displayMeta.setScale(SCALE_VEC);

        displayEntity.setInstance(player.getInstance(), spawnPos).thenRun(() -> {
            displayEntity.teleport(displayEntity.getPosition().add(-SCALE * 0.5f, 0, SCALE * 0.5f));
            displayEntity.lookAt(displayEntity.getPosition().add(1, 0, 0));
        });

        // Init inventory and event handlers
        lootScreen = new Inventory(InventoryType.CHEST_2_ROW, title);
        lootScreenEventHandler = EventNode.type("click_loot_screen", EventFilter.INVENTORY, (event, inv) -> inv == lootScreen)
                .addListener(InventoryClickEvent.class, this::onPlayerClickLootScreenSlot)
                .addListener(InventoryCloseEvent.class, this::onPlayerCloseLootScreen);
        DuskfallServer.eventHandler.addChild(lootScreenEventHandler);

        // Populate item slots
        for(ItemStack stack : itemStacks) {
            lootScreen.setItemStack(rand.nextInt(lootScreen.getSize()), stack);
        }
    }

    /**
     * When a player closes the loot screen, play a sound, destroy all loot and stop listening for events.
     * @param event The inventory close event.
     */
    private void onPlayerCloseLootScreen(InventoryCloseEvent event) {
        event.getPlayer().playSound(Sounds.LOOT_BAG_RUSTLE);

        // Delete everything
        lootScreen.clear();
        displayEntity.remove();
        this.remove();

        DuskfallServer.eventHandler.removeChild(lootScreenEventHandler);
    }

    /**
     * Called when a player clicks a slot of the loot screen.
     * This will add the item to their inventory.
     * @param event The inventory click event.
     */
    public void onPlayerClickLootScreenSlot(InventoryClickEvent event) {
        int slot = event.getSlot();
        ItemStack clickedItem = event.getClickedItem();
        ItemStack cursorItem = event.getCursorItem();
        if(!(event.getPlayer() instanceof MmoPlayer player)) {
            return;
        }

        // If we've clicked something
        if(clickedItem != ItemStack.AIR) {
            // Add it
            player.giveItem(clickedItem, ItemGainReason.LOOT);
            // If no cursor item, remove it from cursor as well
            if(cursorItem.isAir()) {
                player.getInventory().setCursorItem(ItemStack.AIR);
            }
        }
        // If the player has an item in their cursor, restore it
        if(cursorItem != ItemStack.AIR) {
            player.getInventory().setCursorItem(cursorItem);
        }
        // Remove item from loot screen
        lootScreen.setItemStack(slot, ItemStack.AIR);

        // If inventory is empty, close screen
        boolean empty = true;
        for(ItemStack stack : lootScreen.getItemStacks()) {
            if(!stack.isAir()) {
                empty = false;
                break;
            }
        }
        if(empty) {
            // TODO: Not working?
            DuskfallServer.logger.info("Force closing loot screen...");
            player.closeInventory(true);
            player.closeInventory();
        }
    }

    /**
     * When a player interacts with this loot bag, play a sound and open the loot screen.
     * @param player The player who opened the loot bag.
     */
    @Override
    public EventListener.Result onPlayerInteract(MmoPlayer player) {
        player.playSound(Sounds.LOOT_BAG_RUSTLE);

        // Open loot screen if the player has no screen open
        if(player.getOpenInventory() == null) {
            player.openInventory(lootScreen);
        }
        return EventListener.Result.SUCCESS;
    }
}
