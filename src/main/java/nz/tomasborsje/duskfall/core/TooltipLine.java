package nz.tomasborsje.duskfall.core;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public class TooltipLine implements Comparable<TooltipLine> {

    private final TooltipPosition position;
    private final Component component;
    private final int orderInGroup;

    public TooltipLine(TooltipPosition position, Component component, int orderInGroup) {
        this.position = position;
        this.component = component;
        this.orderInGroup = orderInGroup;
    }

    public TooltipLine(TooltipPosition position, Component component) {
        this(position, component, 0);
    }

    @Override
    public int compareTo(@NotNull TooltipLine o) {
        int positionComparison = position.getOrder() - o.position.getOrder();

        if (positionComparison == 0) {
            return orderInGroup - o.orderInGroup;
        }
        return positionComparison;
    }

    public TooltipPosition getPosition() {
        return position;
    }

    public Component getComponent() {
        return component;
    }

    public int getOrderInGroup() {
        return orderInGroup;
    }
}
