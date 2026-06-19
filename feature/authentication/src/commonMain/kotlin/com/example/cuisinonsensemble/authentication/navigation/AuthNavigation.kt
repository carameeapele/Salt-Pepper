package com.example.cuisinonsensemble.authentication.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.cuisinonsensemble.authentication.screen.LandingScreen
import com.example.cuisinonsensemble.authentication.screen.LoginScreen
import com.example.cuisinonsensemble.authentication.screen.RegisterScreen
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.authNavigation(
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onLoginSuccess: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    composable<AuthDestinations.Landing> {
        LandingScreen(
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToRegister = onNavigateToRegister
        )
    }

    composable<AuthDestinations.Login> {
        LoginScreen(
            viewModel = koinViewModel(),
            onNavigateToRegister = onNavigateToRegister,
            onLoginSuccess = onLoginSuccess
        )
    }

    composable<AuthDestinations.Register> {
        RegisterScreen(
            viewModel = koinViewModel(),
            onNavigateToLogin = onNavigateToLogin,
            onRegisterSuccess = onRegisterSuccess
        )
    }
}