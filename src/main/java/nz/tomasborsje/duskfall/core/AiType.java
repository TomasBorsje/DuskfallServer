package nz.tomasborsje.duskfall.core;

import com.google.gson.annotations.SerializedName;

public enum AiType {
    @SerializedName("passive") PASSIVE,
    @SerializedName("neutral") NEUTRAL,
    @SerializedName("aggressive") AGGRESSIVE
}
