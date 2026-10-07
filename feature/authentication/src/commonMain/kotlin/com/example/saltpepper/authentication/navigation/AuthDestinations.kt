package com.example.saltpepper.authentication.navigation

import kotlinx.serialization.Serializable

sealed class AuthDestinations {
    @Serializable
    data object  Landing : AuthDestinations()

    @Serializable
    data object Login : AuthDestinations()

    @Serializable
    data object Register : AuthDestinations()

    @Serializable
    data class VerifyEmailCode(val email: String) : AuthDestinations()
}