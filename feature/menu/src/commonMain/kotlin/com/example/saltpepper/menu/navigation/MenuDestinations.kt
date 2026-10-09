package com.example.saltpepper.menu.navigation

import kotlinx.serialization.Serializable

sealed class MenuDestinations {
    @Serializable
    data object Menu : MenuDestinations()
}