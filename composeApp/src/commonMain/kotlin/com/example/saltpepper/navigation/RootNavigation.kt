package com.example.saltpepper.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.saltpepper.HomeDestination
import com.example.saltpepper.authentication.navigation.AuthDestinations
import com.example.saltpepper.authentication.navigation.authNavigation
import com.example.saltpepper.home.HomeScreen
import com.example.saltpepper.splash.navigation.SplashDestination
import com.example.saltpepper.splash.navigation.splashNavigation

@Composable
fun RootNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = SplashDestination,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
    ) {
        composable<HomeDestination> {
            HomeScreen()
        }

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
            onNavigateToVerifyCode = { email ->
                navController.navigate(AuthDestinations.VerifyEmailCode(email))
            },
            onLoginSuccess = {
                navController.navigate(HomeDestination) {
                    popUpTo(AuthDestinations.Landing) { inclusive = true }
                }
            },
            onRegisterSuccess = {
                navController.navigate(HomeDestination) {
                    popUpTo(AuthDestinations.Landing) { inclusive = true }
                }
            },
            onNavigateBackFromVerification = { navController.popBackStack() },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}