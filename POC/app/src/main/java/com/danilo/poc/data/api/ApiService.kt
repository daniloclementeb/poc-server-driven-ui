package com.danilo.poc.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @POST("v1/login")
    suspend fun login(@Body loginRequest:Map<String, String>): Response<Map<String, Object>>

    @GET
    suspend fun dynamicRequest(@Url url:String):Response<Map<String, Object>>
}