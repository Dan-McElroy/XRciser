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

/**
 * Represents a remote service that provides details about exercises.
 */
interface ExerciseService {

    /**
     * Loads the first page of exercises, with an optional specific category.
     *
     * @param categoryId The ID of a category to filter results by. If null, no filter will be applied.
     * @return A paged result with a list of exercises, and links to the next and previous pages if applicable.
     */
    @GET("exercise?status=2&language=2")
    fun getPage(@Query("category") categoryId: Int? = null) : Maybe<PagedResult<Exercise>>

    /**
     * Loads a new page from an exercise result.
     *
     * @param url The URL of a new page, typically found within a [PagedResult].
     * @return A paged result with a list of exercises, and links to the next and previous pages if applicable.
     */
    @GET
    fun getPage(@Url url: String) : Maybe<PagedResult<Exercise>>

    /**
     * Loads all information for a given exercise.
     *
     * @param id The ID of the exercise.
     * @return An object containing all information about an exercise.
     */
    @GET("exerciseinfo/{id}")
    fun getInfo(@Path("id") id: Int) : Maybe<ExerciseInfo>

    /**
     * Loads all information for a given piece of equipment.
     *
     * @param id The ID of the equipment.
     * @return An object containing all information about the equipment.
     */
    @GET("equipment/{id}")
    fun getEquipment(@Path("id") id: Int) : Observable<Equipment>

    /**
     * Loads all information for a given muscle.
     *
     * @param id The ID of the muscle.
     * @return An object containing all information about the muscle.
     */
    @GET("muscle/{id}")
    fun getMuscle(@Path("id") id: Int) : Observable<Muscle>


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