package com.example.cuisinonsensemble.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.cuisinonsensemble.splash.screen.SplashScreen
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.splashNavigation(
    onNavigateToLogin: () -> Unit
) {
    composable<SplashDestination> {
        SplashScreen(
            viewModel = koinViewModel(),
            onNavigateToLogin = onNavigateToLogin,
        )
    }
}