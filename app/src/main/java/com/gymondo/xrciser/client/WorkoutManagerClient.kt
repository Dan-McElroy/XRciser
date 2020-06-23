package com.gymondo.xrciser.client

import com.google.gson.GsonBuilder
import com.gymondo.xrciser.services.WorkoutManagerService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

//object WorkoutManagerClient {
//
//    val service(): WorkoutManagerService
//            get() {
//        val gson = GsonBuilder()
//            .setLenient()
//            .create()
//        val interceptor = HttpLoggingInterceptor()
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
//        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
//
//        return WorkoutManagerService :
//        }
//    }
//}