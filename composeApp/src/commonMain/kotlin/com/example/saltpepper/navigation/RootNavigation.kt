package com.example.saltpepper.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.saltpepper.authentication.navigation.AuthDestinations
import com.example.saltpepper.authentication.navigation.authNavigation
import com.example.saltpepper.data.repository.AuthRepository
import com.example.saltpepper.splash.navigation.SplashDestination
import com.example.saltpepper.splash.navigation.splashNavigation
import com.example.saltpepper.ui.MainScreen
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

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
            },
            onNavigateToMain = {
                navController.navigate(MainDestination) {
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
                navController.navigate(MainDestination) {
                    popUpTo(AuthDestinations.Landing) { inclusive = true }
                }
            },
            onRegisterSuccess = {
                navController.navigate(MainDestination) {
                    popUpTo(AuthDestinations.Landing) { inclusive = true }
                }
            },
            onNavigateBackFromVerification = { navController.popBackStack() },
            onNavigateBack = {
                navController.popBackStack()
            }
        )
        composable<MainDestination> {
            val lifecycleOwner = LocalLifecycleOwner.current
            val authRepository = koinInject<AuthRepository>()
            LaunchedEffect(lifecycleOwner) {
                lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    while (true) {
                        val result = authRepository.restoreSession()
                        if (result.isSuccess && result.getOrNull() == null) {
                            navController.navigate(AuthDestinations.Landing) {
                                popUpTo(MainDestination) { inclusive = true }
                            }
                            return@repeatOnLifecycle
                        }
                        delay(30_000)
                    }
                }
            }
            MainScreen()
        }
    }
}