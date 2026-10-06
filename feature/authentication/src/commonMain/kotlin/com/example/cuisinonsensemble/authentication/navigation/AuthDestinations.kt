package com.example.cuisinonsensemble.authentication.navigation

import kotlinx.serialization.Serializable

sealed class AuthDestinations {
    @Serializable
    data object  Landing : AuthDestinations()

    @Serializable
    data object Login : AuthDestinations()

    @Serializable
    data object Register : AuthDestinations()
}