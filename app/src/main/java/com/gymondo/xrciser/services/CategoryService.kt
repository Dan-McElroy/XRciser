package com.gymondo.xrciser.services

import com.gymondo.xrciser.data.Category
import com.gymondo.xrciser.data.PagedResult
import io.reactivex.Maybe
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

/**
 * Represents a remote service that provides details about exercise categories.
 */
interface CategoryService {

    /**
     * Loads all information for a given category.
     *
     * @param id The ID of the category.
     * @return An object containing all information about a category.
     */
    @GET("exercisecategory/{id}")
    fun getCategory(@Path("id") id: Int) : Maybe<Category>

    /**
     * Loads the first page of categories.
     *
     * @return A paged result with a list of exercises, and links to the next and previous pages if applicable.
     */
    @GET("exercisecategory")
    fun getPage() : Maybe<PagedResult<Category>>

    /**
     * Loads a new page from a category result.
     *
     * @param url The URL of a new page, typically found within a [PagedResult].
     * @return A paged result with a list of categories, and links to the next and previous pages if applicable.
     */
    @GET
    fun getPage(@Url url: String) : Maybe<PagedResult<Category>>

    companion object {
        fun create(): CategoryService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://wger.de/api/v2/")
                .client(getClient())
                .build()
            return retrofit.create(CategoryService::class.java)
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