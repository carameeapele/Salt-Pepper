package com.example.cuisinonsensemble.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.cuisinonsensemble.authentication.navigation.AuthDestinations
import com.example.cuisinonsensemble.authentication.navigation.authNavigation

@Composable
fun RootNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AuthDestinations.Login
    ) {
        authNavigation(
            onNavigateToRegister = {
                navController.navigate(AuthDestinations.Register) {
                    popUpTo(AuthDestinations.Login) { saveState = true }
                }
            },
            onNavigateToLogin = {
                navController.navigate(AuthDestinations.Login) {
                    popUpTo(AuthDestinations.Register) { inclusive = true }
                }
            }
        )
    }
}