package nz.tomasborsje.duskfall.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.core.MmoCreature;

public class SpawnEntityCommand extends Command {

    public SpawnEntityCommand() {
        super("spawnentity");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Usage: /spawnentity <entity_id>");
        });

        var entityIdArg = ArgumentType.String("entity-id");

        // Callback executed if the argument has been wrongly used
        entityIdArg.setCallback((sender, exception) -> {
            final String input = exception.getInput();
            sender.sendMessage("The given entity iD " + input + " is invalid!");
        });

        addSyntax((sender, context) -> {
            final String entityId = context.get(entityIdArg);

            // Create entity
            // TODO: Use entity ID and lookup entity registry
            EntityCreature zombie = new MmoCreature(EntityType.ZOMBIE, 2);
            Pos spawnPosition = new Pos(0D, 42D, 0D);

            // Add to world
            zombie.setInstance(DuskfallServer.overworldInstance, spawnPosition);
        }, entityIdArg);
    }
}