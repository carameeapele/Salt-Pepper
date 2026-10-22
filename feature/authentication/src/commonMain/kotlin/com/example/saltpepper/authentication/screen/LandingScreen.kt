package com.example.saltpepper.authentication.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.saltpepper.core.ui.component.button.ButtonVariant
import com.example.saltpepper.core.ui.component.button.CustomOutlinedButton
import com.example.saltpepper.core.ui.component.button.PrimaryButton
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.app_name
import saltpepper.core.ui.generated.resources.logomark

@Composable
fun LandingScreen(
    onNavigateToLogin : () -> Unit,
    onNavigateToRegister : () -> Unit
) {
    LandingScreenContent(
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToRegister = onNavigateToRegister
    )
}

@Composable
fun LandingScreenContent(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff263710))
            .padding(horizontal = 24.dp, vertical = 48.dp)
    ) {
        Column(
            modifier = modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.logomark),
                contentDescription = stringResource(Res.string.app_name),
                modifier = Modifier.size(200.dp)
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PrimaryButton(
                modifier = modifier.fillMaxWidth(),
                buttonVariant = ButtonVariant.GIANT,
                onClick = onNavigateToLogin,
                text = "Se connecter"
            )
            CustomOutlinedButton(
                modifier = modifier.fillMaxWidth(),
                buttonVariant = ButtonVariant.GIANT,
                onClick = onNavigateToRegister,
                text = "S'inscrire",
                borderColor = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Preview
@Composable
fun LandingScreenPreview() {
    SaltPepperTheme {
        LandingScreenContent()
    }
}