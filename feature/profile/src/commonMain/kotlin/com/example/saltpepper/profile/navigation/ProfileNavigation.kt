package com.example.saltpepper.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.saltpepper.profile.screen.ProfileScreen

fun NavGraphBuilder.profileNavigation() {
    composable<ProfileDestinations.Profile> {
        ProfileScreen()
    }
}