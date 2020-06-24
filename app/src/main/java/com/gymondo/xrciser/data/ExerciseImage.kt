package com.gymondo.xrciser.data

import com.google.gson.annotations.SerializedName

data class ExerciseImage (
    val id: Int,
    @SerializedName("license_author")
    val licenseAuthor: String,
    val status: String,
    @SerializedName("image")
    val url: String,
    val isMain: Boolean,
    val license: Int,
    @SerializedName("exercise")
    val exerciseId: Int
)