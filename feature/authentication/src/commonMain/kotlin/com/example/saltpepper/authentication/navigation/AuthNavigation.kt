package com.example.saltpepper.authentication.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.saltpepper.authentication.screen.LandingScreen
import com.example.saltpepper.authentication.screen.LoginScreen
import com.example.saltpepper.authentication.screen.RegisterScreen
import com.example.saltpepper.authentication.screen.VerifyEmailCodeScreen

fun NavGraphBuilder.authNavigation(
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToVerifyCode: (String) -> Unit,
    onLoginSuccess: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateBackFromVerification: () -> Unit
) {
    composable<AuthDestinations.Landing> {
        LandingScreen(
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToRegister = onNavigateToRegister
        )
    }

    composable<AuthDestinations.Login> {
        LoginScreen(
            onNavigateToRegister = onNavigateToRegister,
            onNavigateBack = onNavigateBack,
            onNavigateToVerifyCode = onNavigateToVerifyCode
        )
    }

    composable<AuthDestinations.VerifyEmailCode> { backStackEntry: NavBackStackEntry ->
        val destination = backStackEntry.toRoute<AuthDestinations.VerifyEmailCode>()
        VerifyEmailCodeScreen(
            email = destination.email,
            onVerified = onLoginSuccess,
            onBack = onNavigateBackFromVerification
        )
    }

    composable<AuthDestinations.Register> {
        RegisterScreen(
            onNavigateToLogin = onNavigateToLogin,
            onRegisterSuccess = onRegisterSuccess,
            onNavigateBack = onNavigateBack
        )
    }
}