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
    onNavigateToBrowseRecipes: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToFavoriteRecipes: () -> Unit
) {
    composable<ProfileDestinations.Profile> {
        ProfileScreen(
            onNavigateToSettings = onNavigateToSettings,
            onHouseholdEdit = onHouseholdEdit,
            onNavigateToFavoriteRecipes = onNavigateToFavoriteRecipes
        )
    }
    composable<ProfileDestinations.Settings> {
        SettingsScreen(
            onLogout = onLogout,
            onAccountDeleted = onAccountDeleted
        )
    }
    composable<ProfileDestinations.Favorites> {
        FavoritesScreen(
            onNavigateToBrowseRecipes = onNavigateToBrowseRecipes
        )
    }
}