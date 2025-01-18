package nz.tomasborsje.duskfall.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import nz.tomasborsje.duskfall.core.ItemGainReason;
import nz.tomasborsje.duskfall.definitions.items.ItemDefinition;
import nz.tomasborsje.duskfall.entities.MmoPlayer;
import nz.tomasborsje.duskfall.registry.Registries;

public class GiveItemCommand extends Command {

    public GiveItemCommand() {
        super("giveitem");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Usage: /giveitem <item_id>");
        });

        var itemIdArg = ArgumentType.String("item-id");

        // Callback executed if the argument has been wrongly used
        itemIdArg.setCallback((sender, exception) -> {
            final String input = exception.getInput();
            sender.sendMessage("The given item ID " + input + " is invalid!");
        });

        addSyntax((sender, context) -> {
            final String itemId = context.get(itemIdArg);
            ItemDefinition itemDefinition = Registries.ITEMS.get(itemId);
            if(itemDefinition == null) {
                sender.sendMessage("The given item ID "+itemId+" does not exist!");
                return;
            }

            // Add debuff to command sender as well
            if(sender instanceof MmoPlayer player) {
                player.giveItem(itemDefinition.buildItemStack(), ItemGainReason.GET);
            }
        }, itemIdArg);
    }
}