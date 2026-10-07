package com.example.saltpepper.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.saltpepper.authentication.navigation.AuthDestinations
import com.example.saltpepper.authentication.navigation.authNavigation
import com.example.saltpepper.home.navigation.HomeDestinations
import com.example.saltpepper.home.navigation.homeNavigation
import com.example.saltpepper.menu.navigation.menuNavigation
import com.example.saltpepper.profile.navigation.profileNavigation
import com.example.saltpepper.recipes.navigation.recipesNavigation
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
                navController.navigate(HomeDestinations.Home) {
                    popUpTo(AuthDestinations.Landing) { inclusive = true }
                }
            },
            onRegisterSuccess = {
                navController.navigate(HomeDestinations.Home) {
                    popUpTo(AuthDestinations.Landing) { inclusive = true }
                }
            },
            onNavigateBackFromVerification = { navController.popBackStack() },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
        homeNavigation()
        menuNavigation()
        profileNavigation()
        recipesNavigation()
    }
}