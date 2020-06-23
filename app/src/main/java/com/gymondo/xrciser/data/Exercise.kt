package com.gymondo.xrciser.data

import com.google.gson.annotations.SerializedName

data class Exercise (
    val id: String,
    @SerializedName("license_author")
    val licenseAuthor: String,
    val status: String,
    val description: String,
    val name: String,
    @SerializedName("name_original")
    val originalName : String,
    @SerializedName("creation_date")
    val creationDate : String, // TODO: Date?
    val uuid: String,
    val license: Int, // TODO: Enum
    val category: Int, // TODO: Enum
    val language: Int, // TODO: Enum
    val muscles: List<Int>, // TODO: Array<Enum>
    @SerializedName("muscles_secondary")
    val secondaryMuscles: List<Int>,   // TODO: Enum
    val equipment: List<Int> // TODO: Enum
)