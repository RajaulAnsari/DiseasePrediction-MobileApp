package com.example.diseasepredictionssystem

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class  PredictRequest(
    val symptoms: List<String>,
    val token: String
)