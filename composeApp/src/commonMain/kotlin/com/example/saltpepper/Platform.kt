package com.example.saltpepper

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform