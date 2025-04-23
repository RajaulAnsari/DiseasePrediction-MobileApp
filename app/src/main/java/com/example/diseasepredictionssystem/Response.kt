package com.example.diseasepredictionssystem
data class LoginResponse(
    val token: String?,
    val message: String
)
data class RegisterResponse(
    val message: String,
    val success: Boolean
)

 data class PredictResponse(
    val predicted_disease: String,
    val description: String,
    val precautions: List<String>,
    val medications: List<String>,
    val diets: List<String>,
    val workouts: List<String>
)

