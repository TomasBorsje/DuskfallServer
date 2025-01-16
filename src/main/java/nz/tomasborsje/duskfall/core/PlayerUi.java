package nz.tomasborsje.duskfall.core;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.scoreboard.Sidebar;
import nz.tomasborsje.duskfall.entities.MmoPlayer;

public class PlayerUi {
    private Sidebar sidebar;
    private final Sidebar.ScoreboardLine belowTitleSpace = new Sidebar.ScoreboardLine("below_title", Component.empty(), 9);
    private final Sidebar.ScoreboardLine meleeDamage = new Sidebar.ScoreboardLine("melee_damage_display", Component.empty(), 8);
    private final MmoPlayer player;
    boolean init;

    public PlayerUi(MmoPlayer player) {
        this.player = player;
    }

    /**
     * Renders and updates the player's MMO HUD (health bar, stats above hot-bar, sidebar, etc.).
     */
    public void render() {
        if(!init) {
            init();
        }
        // Set health bar to display player health
        StatContainer stats = player.getStats();
        player.setHealth(stats.getCurrentHealth() / (float) stats.getMaxHealth() * 19 + 1); // TODO: Interrupt play out packet?

        // Show health to player
        Component healthBar = Component.text(TextIcons.HEART+" ", NamedTextColor.RED).append(Component.text(stats.getCurrentHealth() + " / " + stats.getMaxHealth() + " Melee: " + stats.getMeleeDamage(), NamedTextColor.WHITE));
        player.sendActionBar(healthBar);

        updateSidebarLines();
    }

    /**
     * Initialize the player UI. Note that this cannot be done during the constructor as packets aren't yet registered
     * at that point.
     */
    private void init() {
        init = true;

        this.sidebar = new Sidebar(Component.text("Duskfall", Style.style().decoration(TextDecoration.BOLD, true).color(NamedTextColor.GOLD).build()));
        sidebar.addViewer(player);
        sidebar.createLine(belowTitleSpace);
        sidebar.createLine(meleeDamage);

    }

    private void updateSidebarLines() {
        Component meleeDamageComponent = Component.text(TextIcons.SWORD, NamedTextColor.RED).append(Component.text(": "+player.getStats().getMeleeDamage(), NamedTextColor.WHITE));
        sidebar.updateLineContent(meleeDamage.getId(), meleeDamageComponent);
    }
}
