package com.gymondo.xrciser.services

import com.gymondo.xrciser.data.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface WorkoutManagerService {

    @GET("exercise")
    fun getAllExercises() : Call<ExerciseResult>

    companion object {
        fun create(): WorkoutManagerService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://wger.de/api/v2/")
                .build()
            return retrofit.create(WorkoutManagerService::class.java)
        }
    }
}