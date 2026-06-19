package com.example.cuisinonsensemble.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.cuisinonsensemble.authentication.navigation.AuthDestinations
import com.example.cuisinonsensemble.authentication.navigation.authNavigation
import com.example.cuisinonsensemble.splash.navigation.SplashDestination
import com.example.cuisinonsensemble.splash.navigation.splashNavigation

@Composable
fun RootNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = SplashDestination
    ) {
        splashNavigation(
            onNavigateToLanding = {
                navController.navigate(AuthDestinations.Landing) {
                    popUpTo(SplashDestination) { inclusive = true }
                }
            }
        )
        authNavigation(
            onNavigateToRegister = {
                navController.navigate(AuthDestinations.Register) {
                    popUpTo(AuthDestinations.Login) { inclusive = true }
                }
            },
            onNavigateToLogin = {
                navController.navigate(AuthDestinations.Login) {
                    popUpTo(AuthDestinations.Register) { inclusive = true }
                }
            },
            onLoginSuccess = { },
            onRegisterSuccess = { },
            onNavigateBack = { }
        )
    }
}