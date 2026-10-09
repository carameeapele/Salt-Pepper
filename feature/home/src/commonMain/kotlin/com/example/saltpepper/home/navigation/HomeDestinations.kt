package com.example.saltpepper.home.navigation

import kotlinx.serialization.Serializable

sealed class HomeDestinations {
    @Serializable
    data object Home : HomeDestinations()
}