package com.example.saltpepper.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.saltpepper.authentication.navigation.AuthDestinations
import com.example.saltpepper.authentication.navigation.authNavigation
import com.example.saltpepper.data.repository.AuthRepository
import com.example.saltpepper.profile.screen.HouseholdSetupAfterRegistrationScreen
import com.example.saltpepper.profile.viewmodel.HouseholdSetupAfterRegistrationViewModel
import com.example.saltpepper.splash.navigation.SplashDestination
import com.example.saltpepper.splash.navigation.splashNavigation
import com.example.saltpepper.ui.MainScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.logout_error_message
import saltpepper.core.ui.generated.resources.ok

@Serializable
private data class HouseholdSetupDestination(val fromProfile: Boolean = false)

val AppScreenPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp)

@Composable
fun RootNavigation(
    navController: NavHostController = rememberNavController()
) {
    var logoutError by remember { mutableStateOf(false) }
    val setupViewModel: HouseholdSetupAfterRegistrationViewModel = koinViewModel()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val isMainDestination = backStackEntry?.destination?.hasRoute<MainDestination>() == true
    Box(Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = SplashDestination,
            modifier = if (isMainDestination) Modifier else Modifier.padding(AppScreenPadding),
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
                onNavigateToRegistrationVerification = { email, name ->
                    navController.navigate(AuthDestinations.VerifyRegistrationCode(email, name))
                },
                onLoginSuccess = {
                    setupViewModel.reset()
                    navController.navigate(MainDestination) {
                        popUpTo(AuthDestinations.Landing) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    setupViewModel.reset()
                    navController.navigate(HouseholdSetupDestination()) {
                        popUpTo(AuthDestinations.Landing) { inclusive = true }
                    }
                },
                onNavigateBackFromVerification = { navController.popBackStack() },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
            composable<HouseholdSetupDestination> { entry ->
                val destination = entry.toRoute<HouseholdSetupDestination>()
                HouseholdSetupAfterRegistrationScreen(
                    viewModel = setupViewModel,
                    onExit = {
                        if (destination.fromProfile) {
                            navController.popBackStack()
                        } else {
                            navController.navigate(MainDestination) {
                                popUpTo<HouseholdSetupDestination> { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable<MainDestination> {
                val lifecycleOwner = LocalLifecycleOwner.current
                val authRepository = koinInject<AuthRepository>()
                val coroutineScope = rememberCoroutineScope()
                LaunchedEffect(lifecycleOwner) {
                    lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        while (true) {
                            val result = authRepository.restoreSession()
                            if (result.isSuccess && result.getOrNull() == null) {
                                setupViewModel.reset()
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
                    onHouseholdEdit = {
                        setupViewModel.start()
                        navController.navigate(HouseholdSetupDestination(fromProfile = true))
                    },
                    onAccountDeleted = {
                        setupViewModel.reset()
                        navController.navigate(AuthDestinations.Landing) {
                            popUpTo(MainDestination) { inclusive = true }
                        }
                    },
                    onLogout = {
                        coroutineScope.launch {
                            logoutError = authRepository.logout().isFailure
                            setupViewModel.reset()
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