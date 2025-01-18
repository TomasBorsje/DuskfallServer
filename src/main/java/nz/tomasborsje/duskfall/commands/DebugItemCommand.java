package nz.tomasborsje.duskfall.commands;

import net.minestom.server.command.builder.Command;
import nz.tomasborsje.duskfall.entities.MmoPlayer;

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