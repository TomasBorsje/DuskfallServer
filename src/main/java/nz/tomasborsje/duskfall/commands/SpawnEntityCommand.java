package nz.tomasborsje.duskfall.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.definitions.entities.EntityDefinition;
import nz.tomasborsje.duskfall.entities.MmoCreature;
import nz.tomasborsje.duskfall.registry.Registries;

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
            sender.sendMessage("The given entity ID " + input + " is invalid!");
        });

        addSyntax((sender, context) -> {
            final String entityId = context.get(entityIdArg);
            EntityDefinition entityDef = Registries.ENTITIES.get(entityId);

            if(entityDef == null) {
                sender.sendMessage("The given entity ID "+entityId+"does not exist!");
                return;
            }

            // Create entity
            Pos spawnPosition = new Pos(3, 42, 3);
            MmoCreature creature = new MmoCreature(entityDef, spawnPosition);

            // Add to world
            creature.setInstance(DuskfallServer.overworldInstance);
        }, entityIdArg);
    }
}