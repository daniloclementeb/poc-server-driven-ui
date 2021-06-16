package com.example.urlshortener.data.api

import com.example.poc.data.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiAdapter () {
    val apiClient: ApiService = provideRetrofit("https://ajgqwbz6h3.execute-api.us-east-1.amazonaws.com/dev/")

    private fun provideRetrofit(
        BASE_URL:String): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).client(okHttpClient).build().create(ApiService::class.java)
    }

}