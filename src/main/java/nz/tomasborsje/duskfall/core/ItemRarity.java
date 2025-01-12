package nz.tomasborsje.duskfall.core;

import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import nz.tomasborsje.duskfall.util.MmoStyles;

public enum ItemRarity {
    @SerializedName("trash") TRASH(MmoStyles.WORTHLESS_STYLE),
    @SerializedName("common") COMMON(MmoStyles.COMMON_STYLE),
    @SerializedName("uncommon") UNCOMMON(MmoStyles.UNCOMMON_STYLE),
    @SerializedName("rare") RARE(MmoStyles.RARE_STYLE),
    @SerializedName("epic") EPIC(MmoStyles.EPIC_STYLE),
    @SerializedName("legendary") LEGENDARY(MmoStyles.LEGENDARY_STYLE);

    public final Style nameStyle;

    ItemRarity(Style nameStyle) {
        this.nameStyle = nameStyle;
    }
}
