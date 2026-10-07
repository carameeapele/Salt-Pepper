package com.example.cuisinonsensemble.authentication.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
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
import com.example.cuisinonsensemble.authentication.model.RegisterScreenUiModel
import com.example.cuisinonsensemble.authentication.viewmodel.RegisterScreenEvent
import com.example.cuisinonsensemble.authentication.viewmodel.RegisterScreenViewModel
import com.example.cuisinonsensemble.core.ui.component.button.ButtonVariant
import com.example.cuisinonsensemble.core.ui.component.button.CustomTextButton
import com.example.cuisinonsensemble.core.ui.component.popup.CustomPopUp
import com.example.cuisinonsensemble.core.ui.component.textfield.CustomOutlinedTextField
import com.example.cuisinonsensemble.core.ui.theme.SaltPepperTheme
import cuisinonsensemble.core.ui.generated.resources.Res
import cuisinonsensemble.core.ui.generated.resources.already_registered_helper_text
import cuisinonsensemble.core.ui.generated.resources.back
import cuisinonsensemble.core.ui.generated.resources.confirm_password_label
import cuisinonsensemble.core.ui.generated.resources.email_label
import cuisinonsensemble.core.ui.generated.resources.email_placeholder
import cuisinonsensemble.core.ui.generated.resources.error_dialog_title
import cuisinonsensemble.core.ui.generated.resources.ok
import cuisinonsensemble.core.ui.generated.resources.password_label
import cuisinonsensemble.core.ui.generated.resources.password_placeholder
import cuisinonsensemble.core.ui.generated.resources.register_button_text
import cuisinonsensemble.core.ui.generated.resources.register_subtitle
import cuisinonsensemble.core.ui.generated.resources.register_title
import cuisinonsensemble.core.ui.generated.resources.signin_redirection_button_text
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterScreenViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiModel by viewModel.uiModel.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                RegisterScreenEvent.RegisterSucceeded -> onRegisterSuccess()
            }
        }
    }

    RegisterScreenContent(
        uiModel = uiModel,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onRegisterClick = viewModel::register,
        onDismissSubmitError = viewModel::dismissSubmitError,
        onNavigateBack = onNavigateBack,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegisterScreenContent(
    modifier: Modifier = Modifier,
    uiModel: RegisterScreenUiModel = RegisterScreenUiModel(),
    onNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onDismissSubmitError: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val isLoading = uiModel.isLoading

    uiModel.submitErrorMessage?.let { message ->
        CustomPopUp(
            title = stringResource(Res.string.error_dialog_title),
            message = message,
            icon = Icons.Default.ErrorOutline,
            confirmButtonText = stringResource(Res.string.ok),
            onDismiss = onDismissSubmitError,
            onConfirm = onDismissSubmitError
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 40.dp)
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

            Spacer(modifier = modifier.height(28.dp))

            AuthHeadline(
                title = stringResource(Res.string.register_title),
                subtitle = stringResource(Res.string.register_subtitle)
            )

            Spacer(modifier = modifier.height(52.dp))

            CustomOutlinedTextField(
                value = uiModel.email,
                onValueChange = onEmailChange,
                label = stringResource(Res.string.email_label),
                placeholder = stringResource(Res.string.email_placeholder),
                leadingIcon = Icons.Outlined.Email,
                keyboardType = KeyboardType.Email,
                helperText = uiModel.emailErrorMessage,
                isError = uiModel.emailErrorMessage != null,
                enabled = !isLoading
            )

            Spacer(modifier = modifier.height(16.dp))

            CustomOutlinedTextField(
                value = uiModel.password,
                onValueChange = onPasswordChange,
                label = stringResource(Res.string.password_label),
                placeholder = stringResource(Res.string.password_placeholder),
                leadingIcon = Icons.Outlined.Lock,
                visibilityToggle = true,
                keyboardType = KeyboardType.Password,
                helperText = uiModel.passwordErrorMessage,
                isError = uiModel.passwordErrorMessage != null,
                enabled = !isLoading

            )

            Spacer(modifier = modifier.height(16.dp))

            CustomOutlinedTextField(
                value = uiModel.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = stringResource(Res.string.confirm_password_label),
                placeholder = stringResource(Res.string.password_placeholder),
                leadingIcon = Icons.Outlined.Lock,
                visibilityToggle = true,
                keyboardType = KeyboardType.Password,
                helperText = uiModel.confirmPasswordErrorMessage,
                isError = uiModel.confirmPasswordErrorMessage != null,
                enabled = !isLoading
            )
        }
        Column(
            modifier = modifier.align(Alignment.BottomCenter)
        ) {
            AuthBottomButtons(
                mainButtonText = stringResource(Res.string.register_button_text),
                onMainButtonClick = onRegisterClick,
                helperText = stringResource(Res.string.already_registered_helper_text),
                redirectionButtonText = stringResource(Res.string.signin_redirection_button_text),
                onRedirectionButtonClick = onNavigateToLogin,
                isLoading = isLoading
            )
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    SaltPepperTheme {
        RegisterScreenContent()
    }
}