package com.example.diseasepredictionssystem

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
    @POST("/api/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>
    @POST("/api/predict")
    suspend fun predictDisease(@Body request: PredictRequest): Response<PredictResponse>
}
