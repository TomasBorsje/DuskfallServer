package nz.tomasborsje.duskfall.core;

import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

public enum ItemRarity {
    @SerializedName("trash") TRASH(Style.style(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)),
    @SerializedName("common") COMMON(Style.style(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)),
    @SerializedName("uncommon") UNCOMMON(Style.style(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)),
    @SerializedName("rare") RARE(Style.style(NamedTextColor.BLUE).decoration(TextDecoration.ITALIC, false)),
    @SerializedName("epic") EPIC(Style.style(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)),
    @SerializedName("legendary") LEGENDARY(Style.style(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false));

    public final Style nameStyle;

    ItemRarity(Style nameStyle) {
        this.nameStyle = nameStyle;
    }
}
