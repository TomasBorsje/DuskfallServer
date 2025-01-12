package nz.tomasborsje.duskfall.core;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public record TooltipLine(TooltipPosition position, Component component) implements Comparable<TooltipLine> {
    @Override
    public int compareTo(@NotNull TooltipLine o) {
        // TODO: Compare components as fallback
        return position.getOrder() - o.position.getOrder();
    }
}
