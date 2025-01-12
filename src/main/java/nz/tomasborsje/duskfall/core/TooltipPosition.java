package nz.tomasborsje.duskfall.core;

public enum TooltipPosition {
    ITEM_TYPE(-1),
    DAMAGE_SPACE(0),
    DAMAGE(1),
    STAT_MOD_SPACE(2),
    STAMINA_MOD(3),
    STRENGTH_MOD(5),
    AGILITY_MOD(6),
    INTELLECT_MOD(7),
    EFFECT_SPACE(8),
    UNIQUE_EFFECTS(9),
    SIMPLE_EFFECTS(10),
    DESCRIPTION_SPACE(11),
    DESCRIPTION(12)
    ;

    private final int order;

    TooltipPosition(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
