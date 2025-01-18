package nz.tomasborsje.duskfall.commands;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.entities.LootBagEntity;
import nz.tomasborsje.duskfall.entities.MmoPlayer;
import nz.tomasborsje.duskfall.registry.Registries;

public class SpawnLootBagCommand extends Command {

    public SpawnLootBagCommand() {
        super("spawnlootbag");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Usage: /spawnlootbag <loot-table-id>");
        });

        var lootTableIdArg = ArgumentType.String("loot-table-id");

        // Callback executed if the argument has been wrongly used
        lootTableIdArg.setCallback((sender, exception) -> {
            final String input = exception.getInput();
            sender.sendMessage("The given loot table ID " + input + " is invalid!");
        });

        addSyntax((sender, context) -> {
            final String lootTableId = context.get(lootTableIdArg);

            if(sender instanceof MmoPlayer player) {
                LootBagEntity blockDisplay = new LootBagEntity(Component.text("Loot Bag - "+lootTableId),
                        Registries.ITEMS.getRandomItem().buildItemStack(), Registries.ITEMS.getRandomItem().buildItemStack(), Registries.ITEMS.getRandomItem().buildItemStack());
                blockDisplay.setInstance(DuskfallServer.overworldInstance, player.getPosition());
            }
        }, lootTableIdArg);
    }
}