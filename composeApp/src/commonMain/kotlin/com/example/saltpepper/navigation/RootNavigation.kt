package com.example.saltpepper.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.logout_error_message
import saltpepper.core.ui.generated.resources.ok

@Composable
fun RootNavigation(
    navController: NavHostController = rememberNavController()
) {
    var logoutError by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
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
                val coroutineScope = rememberCoroutineScope()
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
                MainScreen(
                    onLogout = {
                        coroutineScope.launch {
                            logoutError = authRepository.logout().isFailure
                            navController.navigate(AuthDestinations.Landing) {
                                popUpTo(MainDestination) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
        if (logoutError) {
            AlertDialog(
                onDismissRequest = { logoutError = false },
                text = { Text(stringResource(Res.string.logout_error_message)) },
                confirmButton = {
                    TextButton(onClick = { logoutError = false }) {
                        Text(stringResource(Res.string.ok))
                    }
                }
            )
        }
    }
}