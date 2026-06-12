package com.example.cuisinonsensemble

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform