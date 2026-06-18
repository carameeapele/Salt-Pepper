package com.example.cuisinonsensemble.authentication.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.cuisinonsensemble.authentication.viewmodel.RegisterScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterScreenViewModel = koinViewModel(),
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isRegisterSuccessful) {
        if (state.isRegisterSuccessful) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Create account")

        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text(text = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            enabled = !state.isLoading
        )

        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text(text = "Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = !state.isLoading
        )

        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = state.confirmPassword,
            onValueChange = viewModel::onConfirmPasswordChange,
            label = { Text(text = "Confirm password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = !state.isLoading
        )

        state.errorMessage?.let {
            Text(
                text = it,
                modifier = modifier.padding(top = 8.dp)
            )
        }

        Button(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            onClick = viewModel::register,
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = modifier.padding(2.dp)
                )
            } else {
                Text("Register")
            }
        }

        TextButton(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            onClick = onNavigateToLogin,
            enabled = !state.isLoading
        ) {
            Text(text = "Already have an account? Login")
        }
    }
}