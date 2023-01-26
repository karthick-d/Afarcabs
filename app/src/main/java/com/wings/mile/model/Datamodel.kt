package com.wings.mile.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Datamodel(
        @SerializedName("icon") var icon: Int,
        @SerializedName("name") var name: String,
        @SerializedName("checked") var checked: Boolean
)