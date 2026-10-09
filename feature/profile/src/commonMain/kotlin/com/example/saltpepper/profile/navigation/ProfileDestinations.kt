package com.example.saltpepper.profile.navigation

import kotlinx.serialization.Serializable

sealed class ProfileDestinations {
    @Serializable
    data object Profile : ProfileDestinations()
}