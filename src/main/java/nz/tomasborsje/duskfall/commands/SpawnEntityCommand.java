package nz.tomasborsje.duskfall.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.monster.zombie.ZombieMeta;
import net.minestom.server.entity.metadata.other.BoatMeta;
import net.minestom.server.instance.Instance;
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
            sender.sendMessage("The number " + input + " is invalid!");
        });

        addSyntax((sender, context) -> {
            final String entityId = context.get(entityIdArg);

            // Spawn entity
            Instance instance = DuskfallServer.overworldInstance; // instance to spawn a boat in
            Pos spawnPosition = new Pos(0D, 42D, 0D);
            EntityCreature boat = new MmoCreature(EntityType.ZOMBIE);

            // Change meta before init
            ZombieMeta meta = (ZombieMeta) boat.getEntityMeta();
            meta.setNotifyAboutChanges(false); // this
            meta.setOnFire(true);
            meta.setCustomNameVisible(true);
            meta.setCustomName(Component.text("Dangerous Zombie", NamedTextColor.RED));
            meta.setNotifyAboutChanges(true); // this

            // TODO: modify AI so that the boat is aggressive
            boat.setInstance(instance, spawnPosition); // actually spawning a boat

        }, entityIdArg);

    }
}