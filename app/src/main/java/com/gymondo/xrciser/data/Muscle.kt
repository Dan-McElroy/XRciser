package com.gymondo.xrciser.data

import com.google.gson.annotations.SerializedName
import com.gymondo.xrciser.R.string
import com.gymondo.xrciser.applications.XRciserApp

data class Muscle (
    val id: Int,
    val name: String,
    @SerializedName("is_front")
    val isFront: Boolean
) {
    override fun toString(): String {
        val sideText = XRciserApp.context.getString(
            if (isFront) string.front else string.back
        )
        return "$name $sideText"
    }
}