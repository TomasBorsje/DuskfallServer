package nz.tomasborsje.duskfall.sounds;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

public class Sounds {
    public static final Sound LEVEL_UP = Sound.sound().type(Key.key("ui.toast.challenge_complete")).build();
    public static final Sound LOOT_BAG_RUSTLE = Sound.sound().type(Key.key("item.bundle.drop_contents")).pitch(0.8f).volume(1.5f).build();
    public static final Sound DING = Sound.sound().type(Key.key("entity.experience_orb.pickup")).build();
}
