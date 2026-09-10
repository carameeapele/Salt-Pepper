package com.example.cuisinonsensemble.authentication.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cuisinonsensemble.core.ui.component.button.ButtonVariant
import com.example.cuisinonsensemble.core.ui.component.button.CustomOutlinedButton
import com.example.cuisinonsensemble.core.ui.component.button.PrimaryButton
import com.example.cuisinonsensemble.core.ui.theme.SaltPepperTheme
import cuisinonsensemble.feature.authentication.generated.resources.Res
import cuisinonsensemble.feature.authentication.generated.resources.logosp
import org.jetbrains.compose.resources.painterResource

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
                painter = painterResource(Res.drawable.logosp),
                contentDescription = "Logo",
                modifier = Modifier.size(160.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "salt & pepper",
                color = MaterialTheme.colorScheme.background,
                style = MaterialTheme.typography.displayLarge
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