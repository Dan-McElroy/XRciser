package com.gymondo.xrciser.services

import com.gymondo.xrciser.data.*
import io.reactivex.Maybe
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ExerciseService {


    @GET("exercise?status=2&language=2")
    fun getExercises(@Query("category") categoryId: Int? = null) : Maybe<PagedResult<Exercise>>

    @GET("exerciseinfo/{id}")
    fun getExerciseInfo(@Path("id") id: Int) : Maybe<ExerciseInfo>

    @GET
    fun getExercisePage(@Url url: String) : Maybe<PagedResult<Exercise>>

    @GET("exercisecategory/{id}")
    fun getCategory(@Path("id") id: Int) : Maybe<Category>

    @GET("exercisecategory")
    fun getCategories() : Maybe<PagedResult<Category>>

    @GET
    fun getCategoryPage(@Url url: String) : Maybe<PagedResult<Category>>

    @GET("equipment/{id}")
    fun getEquipment(@Path("id") id: Int) : Observable<Equipment>

    @GET("muscle/{id}")
    fun getMuscle(@Path("id") id: Int) : Observable<Muscle>

    @GET("exerciseimage")
    fun getImages(@Query("exercise") exerciseId: Int) : Maybe<PagedResult<ExerciseImage>>

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