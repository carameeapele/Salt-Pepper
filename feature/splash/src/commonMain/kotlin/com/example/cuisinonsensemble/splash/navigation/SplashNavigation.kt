package com.example.cuisinonsensemble.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.cuisinonsensemble.splash.screen.SplashScreen

fun NavGraphBuilder.splashNavigation(
    onNavigateToLogin: () -> Unit
) {
    composable<SplashDestination> {
        SplashScreen(
            onNavigateToLogin = onNavigateToLogin,
        )
    }
}