package nz.tomasborsje.duskfall.definitions;

import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.text.Component;
import nz.tomasborsje.duskfall.core.TooltipLine;
import nz.tomasborsje.duskfall.core.TooltipPosition;
import nz.tomasborsje.duskfall.util.MmoStyles;

import java.util.List;

public class LetterItemDefinition extends ItemDefinition {

    @SerializedName("author")
    private String author = "Unknown";

    @SerializedName("message")
    private String message = "";

    @Override
    protected void addTooltipLines(List<TooltipLine> tooltipLines) {
        super.addTooltipLines(tooltipLines);
        // TODO: Seems to be in order, but maybe worth enforcing subgroup ordering with a new int in TooltipLine?
        String tooltip = message + "\n\n- "+author;
        for(String str : tooltip.split("(\r\n|\n|\r)")) {
            tooltipLines.add(new TooltipLine(TooltipPosition.LETTER_MESSAGE, Component.text(str, MmoStyles.TRASH_STYLE)));
        }
    }
}
