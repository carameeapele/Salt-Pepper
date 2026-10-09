package com.example.saltpepper.splash.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.saltpepper.splash.viewmodel.SplashScreenUiState
import com.example.saltpepper.splash.viewmodel.SplashScreenViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.retry_action
import saltpepper.core.ui.generated.resources.session_restore_error

@Composable
fun SplashScreen(
    viewModel: SplashScreenViewModel = koinViewModel(),
    onNavigateToLanding: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state) {
        val success = state as? SplashScreenUiState.Success
        if (success != null) {
            if (success.authenticated) onNavigateToMain() else onNavigateToLanding()
        }
    }

    SplashScreenContent(state = state, onRetry = viewModel::retry)
}

@Composable
fun SplashScreenContent(
    state: SplashScreenUiState = SplashScreenUiState.Loading,
    onRetry: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (state == SplashScreenUiState.Error) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(Res.string.session_restore_error),
                    textAlign = TextAlign.Center
                )
                Button(onClick = onRetry) {
                    Text(stringResource(Res.string.retry_action))
                }
            }
        } else {
            CircularProgressIndicator(color = Color.Green)
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreenContent()
    }
}