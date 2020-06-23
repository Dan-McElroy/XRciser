package com.gymondo.xrciser.services

import com.gymondo.xrciser.data.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ExerciseService {

    @GET("exercise")
    fun getExercises() : Call<PagedResult<Exercise>>

    @GET
    fun getPage(@Url url: String) : Call<PagedResult<Exercise>>

    companion object {
        fun create(): ExerciseService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://wger.de/api/v2/")
                .build()
            return retrofit.create(ExerciseService::class.java)
        }
    }
}