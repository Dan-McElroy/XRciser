package com.gymondo.xrciser.data

import com.google.gson.annotations.SerializedName

data class Muscle (
    val id: Int,
    val name: String,
    @SerializedName("is_front")
    val isFront: Boolean
) {
    override fun toString(): String {
        val sideText = if (isFront) "Front" else "Back"
        return "$name (${sideText})"
    }
}