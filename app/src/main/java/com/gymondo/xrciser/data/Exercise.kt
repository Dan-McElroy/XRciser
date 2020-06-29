package com.gymondo.xrciser.data

import com.google.gson.annotations.SerializedName

data class Exercise (
    val id: Int,
    @SerializedName("license_author")
    val licenseAuthor: String,
    val status: String,
    val description: String,
    val name: String,
    @SerializedName("name_original")
    val originalName : String,
    @SerializedName("creation_date")
    val creationDate : String,
    val uuid: String,
    val license: Int,
    val category: Int,
    val language: Int,
    val muscles: List<Int>,
    @SerializedName("muscles_secondary")
    val secondaryMuscles: List<Int>,
    val equipment: List<Int>
) {
    val allMuscles : List<Int>
        get() = muscles + secondaryMuscles

    fun nameMatches(searchTerm: String) : Boolean {
        return name.contains(searchTerm, true) || originalName.contains(searchTerm, true)
    }
}

