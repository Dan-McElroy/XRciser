package com.gymondo.xrciser.data

import com.google.gson.annotations.SerializedName

data class ExerciseInfo (
    val name: String,
    val category: Category,
    val description: String,
    val muscles: List<Muscle>,
    @SerializedName("muscles_secondary")
    val secondaryMuscles: List<Muscle>,
    val equipment: List<Equipment>
) {
    val allMuscles : List<Muscle>
            get() = muscles + secondaryMuscles
}