package com.example.cuisinonsensemble.authentication.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuisinonsensemble.authentication.component.AuthBottomButtons
import com.example.cuisinonsensemble.authentication.component.AuthHeadline
import com.example.cuisinonsensemble.authentication.viewmodel.LoginScreenViewModel
import com.example.cuisinonsensemble.core.ui.component.button.ButtonVariant
import com.example.cuisinonsensemble.core.ui.component.button.TextButton
import com.example.cuisinonsensemble.core.ui.theme.RobotoMonoFont
import com.example.cuisinonsensemble.core.ui.theme.SaltPepperTheme
import org.koin.compose.viewmodel.koinViewModel

private val TextGray = Color(0xff7293A0)
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

            Text(
                text = "Email",
                fontFamily = RobotoMonoFont(),
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                modifier = modifier.fillMaxWidth(),
                value = email,
                onValueChange = onEmailChange,
                placeholder = {
                    Text(
                        text = "jean@mail.com",
                        fontFamily = RobotoMonoFont(),
                        color = TextGray
                    )
                              },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = BorderColor,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary
                ),
                enabled = !isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Password",
                fontFamily = RobotoMonoFont(),
                fontSize = 14.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = onPasswordChange,
                placeholder = { Text("••••••••••••", color = TextGray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible)
                                    Icons.Default.Lock
                                else
                                    Icons.Default.Lock,
                                contentDescription = if (passwordVisible) "Masquer" else "Afficher",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = BorderColor
                ),
                enabled = !isLoading,
                singleLine = true
            )
            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 13.sp
                )
            }
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