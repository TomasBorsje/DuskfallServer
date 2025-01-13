package nz.tomasborsje.duskfall.core;

public enum TooltipPosition {
    ITEM_TYPE(0),
    DAMAGE_SPACE(5),
    DAMAGE(10),
    STAT_MOD_SPACE(15),
    STAMINA_MOD(20),
    STRENGTH_MOD(25),
    AGILITY_MOD(30),
    INTELLECT_MOD(35),
    EFFECT_SPACE(40),
    UNIQUE_EFFECTS(45),
    SIMPLE_EFFECTS(50),
    LETTER_MESSAGE(55),
    DESCRIPTION_SPACE(60),
    DESCRIPTION(65);


    private final int order;

    TooltipPosition(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
