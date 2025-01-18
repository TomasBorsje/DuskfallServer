package nz.tomasborsje.duskfall.core;

import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.text.format.NamedTextColor;

public enum AiType {
    @SerializedName("passive") PASSIVE(NamedTextColor.GREEN),
    @SerializedName("neutral") NEUTRAL(NamedTextColor.YELLOW),
    @SerializedName("aggressive") AGGRESSIVE(NamedTextColor.RED);

    private final NamedTextColor nameColour;

    AiType(NamedTextColor nameColour) {
        this.nameColour = nameColour;
    }

    public NamedTextColor getNameColour() {
        return nameColour;
    }
}
