package nz.tomasborsje.duskfall.core;

import net.minestom.server.event.EventListener;
import nz.tomasborsje.duskfall.entities.MmoPlayer;

public interface InteractableEntity {
    EventListener.Result onPlayerInteract(MmoPlayer player);
}
