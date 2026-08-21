package com.example.cuisinonsensemble.authentication.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cuisinonsensemble.authentication.component.AuthBottomButtons
import com.example.cuisinonsensemble.authentication.component.AuthHeadline
import com.example.cuisinonsensemble.authentication.viewmodel.LoginScreenViewModel
import com.example.cuisinonsensemble.core.ui.component.button.ButtonVariant
import com.example.cuisinonsensemble.core.ui.component.button.TextButton
import com.example.cuisinonsensemble.core.ui.component.textfield.SPOutlinedTextField
import com.example.cuisinonsensemble.core.ui.theme.SaltPepperTheme
import org.koin.compose.viewmodel.koinViewModel

private val BorderColor = Color(0xFFE0E0E0)

@Composable
fun LoginScreen(
    viewModel: LoginScreenViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            onLoginSuccess()
        }
    }

    LoginScreenContent(
        email = state.email,
        password = state.password,
        isLoading = state.isLoading,
        errorMessage = state.errorMessage,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::login,
        onNavigateBack = onNavigateBack,
        onNavigateToRegister = onNavigateToRegister,
        onForgotPassword = { /* TODO */ }
    )
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    email: String = "",
    password: String = "",
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLoginClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 60.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            TextButton(
                buttonVariant = ButtonVariant.MEDIUM,
                onClick = onNavigateBack,
                text = "Retour",
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack
            )

            Spacer(modifier = Modifier.height(46.dp))

            AuthHeadline(
                title = "Bon retour",
                subtitle = "Connectez-vous pour continuer"
            )

            Spacer(modifier = Modifier.height(46.dp))

            SPOutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email",
                placeholder = "jean@mail.com",
                leadingIcon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email,
                helperText = errorMessage,
                isError = false,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(28.dp))

            SPOutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "••••••••••••",
                leadingIcon = Icons.Outlined.Lock,
                visibilityToggle = true,
                keyboardType = KeyboardType.Password,
                helperText = errorMessage,
                isError = false,
                enabled = !isLoading
            )
        }
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AuthBottomButtons(
                mainButtonText ="Se connecter",
                onMainButtonClick = onLoginClick,
                helperText = "Pas encore de compte ?",
                redirectionButtonText = "S'inscrire",
                onRedirectionButtonClick = onNavigateToRegister,
                isLoading = isLoading
            )
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    SaltPepperTheme {
        LoginScreenContent()
    }
}