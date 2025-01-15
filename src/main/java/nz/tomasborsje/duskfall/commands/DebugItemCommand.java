package nz.tomasborsje.duskfall.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import nz.tomasborsje.duskfall.core.ItemGainReason;
import nz.tomasborsje.duskfall.definitions.ItemDefinition;
import nz.tomasborsje.duskfall.entities.MmoPlayer;
import nz.tomasborsje.duskfall.registry.ItemRegistry;

public class DebugItemCommand extends Command {

    public DebugItemCommand() {
        super("debugitem");

        setDefaultExecutor((sender, context) -> {
            // If the sender is a player and they're holding an item, print the NBT
            if(sender instanceof MmoPlayer player) {
                sender.sendMessage(player.getInventory().getItemStack(player.getHeldSlot()).toItemNBT().toString());
            }
        });
    }
}