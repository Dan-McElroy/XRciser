package com.gymondo.xrciser.services

import com.gymondo.xrciser.data.*
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ExerciseService {

    @GET("exercise")
    fun getExercises() : Single<PagedResult<Exercise>>

    @GET
    fun getPage(@Url url: String) : Single<PagedResult<Exercise>>

    @GET("exercisecategory/{id}")
    fun getCategory(@Path("id") id: Int) : Single<Category>

    @GET("equipment/{id}")
    fun getEquipment(@Path("id") id: Int) : Observable<Equipment>

    @GET("muscle/{id}")
    fun getMuscle(@Path("id") id: Int) : Observable<Muscle>

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