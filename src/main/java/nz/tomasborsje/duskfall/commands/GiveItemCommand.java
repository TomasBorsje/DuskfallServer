package nz.tomasborsje.duskfall.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.buffs.Buff;
import nz.tomasborsje.duskfall.buffs.BurningStrengthBuff;
import nz.tomasborsje.duskfall.core.ItemGainReason;
import nz.tomasborsje.duskfall.definitions.ItemDefinition;
import nz.tomasborsje.duskfall.entities.MmoCreature;
import nz.tomasborsje.duskfall.entities.MmoEntity;
import nz.tomasborsje.duskfall.entities.MmoPlayer;
import nz.tomasborsje.duskfall.registry.ItemRegistry;

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
            ItemDefinition itemDefinition = ItemRegistry.Get(itemId);
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