package nz.tomasborsje.duskfall.core;

public enum ItemGainReason {
    LOOT("loot"),
    GET("got");

    /**
     * The verb for how the player got this item (e.g. 'looted', 'traded for').
     */
    public final String gainVerb;

    ItemGainReason(String gainVerb) {
        this.gainVerb = gainVerb;
    }
}
