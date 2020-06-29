package com.gymondo.xrciser.services

import com.gymondo.xrciser.data.ExerciseImage
import com.gymondo.xrciser.data.PagedResult
import io.reactivex.Maybe
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Represents a remote service that provides details about exercise-related images.
 */
interface ImageService {

    /**
     * Loads the first page of images for a given exercise.
     *
     * @param exerciseId The ID of the exercise associated with the images.
     * @return A paged result with a list of images, and links to the next and previous pages if applicable.
     */
    @GET("exerciseimage")
    fun getPage(@Query("exercise") exerciseId: Int) : Maybe<PagedResult<ExerciseImage>>

    /**
     * Loads the first main image for a given exercise.
     *
     * @param exerciseId The ID of the exercise associated with this image.
     * @return A paged result with a single entry containing the first main image for this exercise.
     */
    @GET("exerciseimage?ordering=-is_main&limit=1")
    fun getMain(@Query("exercise") exerciseId: Int) : Maybe<PagedResult<ExerciseImage>>

    companion object {
        fun create(): ImageService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://wger.de/api/v2/")
                .client(getClient())
                .build()
            return retrofit.create(ImageService::class.java)
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