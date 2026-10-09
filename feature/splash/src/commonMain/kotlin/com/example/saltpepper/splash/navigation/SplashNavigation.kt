package com.example.saltpepper.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.saltpepper.splash.screen.SplashScreen

fun NavGraphBuilder.splashNavigation(
    onNavigateToLanding: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    composable<SplashDestination> {
        SplashScreen(
            onNavigateToLanding = onNavigateToLanding,
            onNavigateToMain = onNavigateToMain,
        )
    }
}