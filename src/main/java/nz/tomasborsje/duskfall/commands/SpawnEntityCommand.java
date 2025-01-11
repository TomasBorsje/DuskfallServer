package nz.tomasborsje.duskfall.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import nz.tomasborsje.duskfall.DuskfallServer;
import nz.tomasborsje.duskfall.buffs.Buff;
import nz.tomasborsje.duskfall.buffs.BurningStrengthBuff;
import nz.tomasborsje.duskfall.entities.MmoCreature;
import nz.tomasborsje.duskfall.entities.MmoEntity;

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
            EntityType entityType = EntityType.fromNamespaceId(entityId.toLowerCase());

            // Add debuff to command sender as well
            if(sender instanceof MmoEntity senderEntity) {
                senderEntity.addBuff(new BurningStrengthBuff(senderEntity));
            }

            // Create entity
            // TODO: Use entity ID and lookup entity registry
            Pos spawnPosition = new Pos(3, 42, 3);
            MmoCreature zombie = new MmoCreature(entityType, spawnPosition, 10);

            // Add buff to zombie
            Buff strengthBuff = new BurningStrengthBuff(zombie);
            zombie.addBuff(strengthBuff);

            // Add to world
            zombie.setInstance(DuskfallServer.overworldInstance, spawnPosition);
        }, entityIdArg);
    }
}