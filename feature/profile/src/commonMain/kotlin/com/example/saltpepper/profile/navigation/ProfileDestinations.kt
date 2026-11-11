package com.example.saltpepper.profile.navigation

import kotlinx.serialization.Serializable

sealed class ProfileDestinations {
    @Serializable
    data object Profile : ProfileDestinations()

    @Serializable
    data object Settings : ProfileDestinations()

    @Serializable
    data object Favorites : ProfileDestinations()
}