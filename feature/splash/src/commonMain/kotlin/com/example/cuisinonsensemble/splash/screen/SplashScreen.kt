package com.example.cuisinonsensemble.splash.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

import com.example.cuisinonsensemble.splash.viewmodel.SplashScreenUiState
import com.example.cuisinonsensemble.splash.viewmodel.SplashScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashScreen(
    viewModel: SplashScreenViewModel = koinViewModel(),
    onNavigateToLanding: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state) {
        if (state is SplashScreenUiState.Success) {
            onNavigateToLanding()
        }
    }

    SplashScreenContent()
}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.Green
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreenContent()
    }
}