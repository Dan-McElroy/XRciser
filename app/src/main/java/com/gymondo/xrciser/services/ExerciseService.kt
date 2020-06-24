package com.gymondo.xrciser.services

import com.gymondo.xrciser.data.*
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

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

    @GET("exerciseimage")
    fun getImages(@Query("exercise") exerciseId: Int) : Single<PagedResult<ExerciseImage>>

    companion object {
        fun create(): ExerciseService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://wger.de/api/v2/")
                .client(getClient())
                .build()
            return retrofit.create(ExerciseService::class.java)
        }

        private fun getClient(): OkHttpClient {

            return OkHttpClient.Builder().apply {
                readTimeout(20, TimeUnit.SECONDS)
                writeTimeout(20, TimeUnit.SECONDS)
                connectTimeout(20, TimeUnit.SECONDS)
            }.build()
        }
    }
}