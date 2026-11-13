package com.example.saltpepper.core.network.ktor

data class ApiConfig(
    val baseUrl: String = "https://getsaltpepper.fr/api",
    val requestTimeoutMillis: Long = 15_000,
    val connectTimeoutMillis: Long = 10_000
)
