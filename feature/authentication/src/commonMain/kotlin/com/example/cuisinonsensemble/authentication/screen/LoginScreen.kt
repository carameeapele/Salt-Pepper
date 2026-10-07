package com.example.cuisinonsensemble.authentication.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cuisinonsensemble.authentication.component.AuthBottomButtons
import com.example.cuisinonsensemble.authentication.component.AuthHeadline
import com.example.cuisinonsensemble.authentication.viewmodel.LoginScreenViewModel
import com.example.cuisinonsensemble.core.ui.component.button.ButtonVariant
import com.example.cuisinonsensemble.core.ui.component.button.CustomTextButton
import com.example.cuisinonsensemble.core.ui.component.textfield.CustomOutlinedTextField
import com.example.cuisinonsensemble.core.ui.theme.SaltPepperTheme
import cuisinonsensemble.core.ui.generated.resources.Res
import cuisinonsensemble.core.ui.generated.resources.back
import cuisinonsensemble.core.ui.generated.resources.email_label
import cuisinonsensemble.core.ui.generated.resources.email_placeholder
import cuisinonsensemble.core.ui.generated.resources.login_subtitle
import cuisinonsensemble.core.ui.generated.resources.login_title
import cuisinonsensemble.core.ui.generated.resources.no_account_helper_text
import cuisinonsensemble.core.ui.generated.resources.register_redirection_button_text
import cuisinonsensemble.core.ui.generated.resources.send_code_button_text
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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

    AnimatedContent(
        targetState = state.codeSent,
        transitionSpec = {
            (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                (slideOutHorizontally { width -> -width } + fadeOut())
        },
        label = "emailVerificationWipe"
    ) { showingVerification ->
        if (showingVerification) {
            VerifyEmailCodeScreen(
                email = state.email,
                code = state.code,
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
                onCodeChange = viewModel::onCodeChange,
                onVerify = viewModel::login,
                onResendCode = viewModel::resendCode,
                onBack = viewModel::backToEmail
            )
        } else {
            LoginScreenContent(
                email = state.email,
                isLoading = state.isLoading,
                errorMessage = state.errorMessage,
                onEmailChange = viewModel::onEmailChange,
                onLoginClick = viewModel::login,
                onNavigateBack = onNavigateBack,
                onNavigateToRegister = onNavigateToRegister
            )
        }
    }
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    email: String = "",
    isLoading: Boolean = false,
    errorMessage: String? = null,
    onEmailChange: (String) -> Unit = {},
    onLoginClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 60.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            CustomTextButton(
                buttonVariant = ButtonVariant.MEDIUM,
                onClick = onNavigateBack,
                text = stringResource(Res.string.back),
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack
            )

            Spacer(modifier = Modifier.height(46.dp))

            AuthHeadline(
                title = stringResource(Res.string.login_title),
                subtitle = stringResource(Res.string.login_subtitle)
            )

            Spacer(modifier = Modifier.height(46.dp))

            CustomOutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = stringResource(Res.string.email_label),
                placeholder = stringResource(Res.string.email_placeholder),
                leadingIcon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email,
                helperText = errorMessage,
                isError = errorMessage != null,
                enabled = !isLoading
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            AuthBottomButtons(
                mainButtonText = stringResource(Res.string.send_code_button_text),
                onMainButtonClick = onLoginClick,
                helperText = stringResource(Res.string.no_account_helper_text),
                redirectionButtonText = stringResource(Res.string.register_redirection_button_text),
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