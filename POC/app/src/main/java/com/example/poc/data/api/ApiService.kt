package com.example.poc.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("v1/login")
    suspend fun login(@Body loginRequest:Map<String, String>): Response<Map<String, Object>>

}