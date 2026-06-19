package com.example.cuisinonsensemble.splash.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import com.example.cuisinonsensemble.splash.viewmodel.SplashScreenUiState
import com.example.cuisinonsensemble.splash.viewmodel.SplashScreenViewModel

@Composable
fun SplashScreen(
    viewModel: SplashScreenViewModel,
    onNavigateToLanding: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state) {
        if (state is SplashScreenUiState.Success) {
            onNavigateToLanding()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}