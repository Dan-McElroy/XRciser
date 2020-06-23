package com.gymondo.xrciser.data

data class ExerciseResult (
    val count: Int,
    val next: String,
    val previous: String,
    val results: List<Exercise>
)