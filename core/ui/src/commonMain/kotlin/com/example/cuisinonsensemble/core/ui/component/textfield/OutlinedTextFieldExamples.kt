package com.example.cuisinonsensemble.core.ui.component.textfield

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun OutlinedTextFieldExamples() {
    // Example 1: Simple email field
    val emailState = remember { mutableStateOf("") }
    val emailErrorState = remember { mutableStateOf(false) }

    // Example 2: Password field with visibility toggle
    val passwordState = remember { mutableStateOf("") }
    val passwordVisibleState = remember { mutableStateOf(false) }
    val passwordErrorState = remember { mutableStateOf(false) }

    // Example 3: Username field
    val usernameState = remember { mutableStateOf("") }

    // Example 4: Phone number field
    val phoneState = remember { mutableStateOf("") }
    val phoneErrorState = remember { mutableStateOf(false) }

    Column {
        // Simple email field
        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            placeholder = "Email address",
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            isError = emailErrorState.value,
            errorMessage = if (emailErrorState.value) "Invalid email format" else null
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field with visibility toggle
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            placeholder = "Password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true,
            passwordVisible = passwordVisibleState.value,
            onPasswordVisibilityChange = { passwordVisibleState.value = it },
            isError = passwordErrorState.value,
            errorMessage = if (passwordErrorState.value) "Password must be at least 8 characters" else null
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Username field
        OutlinedTextField(
            value = usernameState.value,
            onValueChange = { usernameState.value = it },
            placeholder = "Username",
            leadingIcon = Icons.Default.Person,
            keyboardType = KeyboardType.Text
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Phone number field with error
        OutlinedTextField(
            value = phoneState.value,
            onValueChange = { phoneState.value = it },
            placeholder = "Phone number",
            keyboardType = KeyboardType.Phone,
            isError = phoneErrorState.value,
            errorMessage = if (phoneErrorState.value) "Invalid phone number" else null
        )
    }
}
