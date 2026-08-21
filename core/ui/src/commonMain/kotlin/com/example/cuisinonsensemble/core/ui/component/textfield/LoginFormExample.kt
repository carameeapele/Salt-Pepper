package com.example.cuisinonsensemble.core.ui.component.textfield

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Reusable OutlinedTextField component with support for:
 * - Password visibility toggle
 * - Error state with custom error messages
 * - Leading icons
 * - Multiple keyboard types
 * - Custom styling with rounded corners
 */

@Composable
fun LoginFormExample() {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val passwordVisibleState = remember { mutableStateOf(false) }
    
    val emailErrorState = remember { mutableStateOf(false) }
    val passwordErrorState = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Email field
        OutlinedTextField(
            value = emailState.value,
            onValueChange = { 
                emailState.value = it
                emailErrorState.value = false
            },
            placeholder = "Email",
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            isError = emailErrorState.value,
            errorMessage = if (emailErrorState.value) "Please enter a valid email" else null
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field with visibility toggle
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { 
                passwordState.value = it
                passwordErrorState.value = false
            },
            placeholder = "Password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            passwordVisible = passwordVisibleState.value,
            onPasswordVisibilityChange = { passwordVisibleState.value = it },
            isError = passwordErrorState.value,
            errorMessage = if (passwordErrorState.value) "Password must be at least 8 characters" else null
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login button would go here
    }
}
