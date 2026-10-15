package com.employeedirectory.app.data.employeedirectory.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Builds the single Retrofit/OkHttp instance used for the Employee Directory feature.
 * Kept as plain factory functions (no DI framework) to keep the project approachable.
 */
object NetworkModule {

    private const val BASE_URL = "https://aamras.com/"

    fun provideGson(): Gson = GsonBuilder().create()

    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    fun provideEmployeeApiService(
        gson: Gson = provideGson(),
        okHttpClient: OkHttpClient = provideOkHttpClient()
    ): EmployeeApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(EmployeeApiService::class.java)
    }
}
