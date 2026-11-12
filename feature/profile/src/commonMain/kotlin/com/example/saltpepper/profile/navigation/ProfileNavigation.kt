package com.example.saltpepper.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.saltpepper.profile.screen.FavoritesScreen
import com.example.saltpepper.profile.screen.ProfileScreen
import com.example.saltpepper.profile.screen.SettingsScreen

fun NavGraphBuilder.profileNavigation(
    onLogout: () -> Unit,
    onAccountDeleted: () -> Unit,
    onHouseholdEdit: () -> Unit,
    onBrowseRecipes: () -> Unit,
    onSettings: () -> Unit,
    onFavorites: () -> Unit,
    onBack: () -> Unit
) {
    composable<ProfileDestinations.Profile> {
        ProfileScreen(
            onSettings = onSettings,
            onHouseholdEdit = onHouseholdEdit,
            onFavorites = onFavorites
        )
    }
    composable<ProfileDestinations.Settings> {
        SettingsScreen(
            onBack = onBack,
            onLogout = onLogout,
            onAccountDeleted = onAccountDeleted
        )
    }
    composable<ProfileDestinations.Favorites> {
        FavoritesScreen(onBack = onBack, onBrowseRecipes = onBrowseRecipes)
    }
}