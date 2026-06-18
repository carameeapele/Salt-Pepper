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
import com.example.cuisinonsensemble.authentication.viewmodel.LoginScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    viewModel: LoginScreenViewModel = koinViewModel(),
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isLoginSuccessful) {
        if (state.isLoginSuccessful) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login")

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = !state.isLoading
        )

        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            enabled = !state.isLoading
        )

        state.errorMessage?.let {
            Text(text = it, modifier = Modifier.padding(top = 8.dp))
        }

        Button(
            onClick = viewModel::login,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(2.dp))
            } else {
                Text("Login")
            }
        }

        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            enabled = !state.isLoading
        ) {
            Text("Don't have an account? Register")
        }
    }
}