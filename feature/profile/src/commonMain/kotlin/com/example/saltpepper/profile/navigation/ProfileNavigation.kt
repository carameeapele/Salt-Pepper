package com.example.saltpepper.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.saltpepper.profile.screen.ProfileScreen
import com.example.saltpepper.profile.screen.SettingsScreen

fun NavGraphBuilder.profileNavigation(
    onLogout: () -> Unit,
    onAccountDeleted: () -> Unit,
    onProfileSetup: () -> Unit,
    onBrowseRecipes: () -> Unit,
    onSettings: () -> Unit,
    onBack: () -> Unit
) {
    composable<ProfileDestinations.Profile> {
        ProfileScreen(
            onSettings = onSettings,
            onProfileSetup = onProfileSetup,
            onBrowseRecipes = onBrowseRecipes
        )
    }
    composable<ProfileDestinations.Settings> {
        SettingsScreen(
            onBack = onBack,
            onLogout = onLogout,
            onAccountDeleted = onAccountDeleted
        )
    }
}