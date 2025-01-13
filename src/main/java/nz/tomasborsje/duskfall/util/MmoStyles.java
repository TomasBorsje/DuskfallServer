package nz.tomasborsje.duskfall.util;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

public class MmoStyles {
    public static final Style DESCRIPTION = Style.style()
            .color(NamedTextColor.DARK_GRAY)
            .decoration(TextDecoration.ITALIC, true)
            .decoration(TextDecoration.BOLD, false)
            .build();

    public static final Style TRASH_STYLE = Style.style()
            .color(NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, false)
            .build();

    public static final Style COMMON_STYLE = Style.style()
            .color(NamedTextColor.WHITE)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, false)
            .build();

    public static final Style UNCOMMON_STYLE = Style.style()
            .color(NamedTextColor.GREEN)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, false)
            .build();

    public static final Style RARE_STYLE = Style.style()
            .color(NamedTextColor.BLUE)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, false)
            .build();

    public static final Style EPIC_STYLE = Style.style()
            .color(NamedTextColor.DARK_PURPLE)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, false)
            .build();

    public static final Style LEGENDARY_STYLE = Style.style()
            .color(NamedTextColor.GOLD)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, true)
            .build();

    public static final Style STAT_MOD_STYLE = Style.style()
            .color(NamedTextColor.GREEN)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, false)
            .build();

    public static final Style ITEM_TYPE = Style.style()
            .color(NamedTextColor.GRAY)
            .decoration(TextDecoration.ITALIC, false)
            .decoration(TextDecoration.BOLD, false)
            .build();
}
