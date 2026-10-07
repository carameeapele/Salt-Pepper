package com.example.saltpepper.authentication.model

data class RegisterScreenUiModel(
    val name: String = "",
    val email: String = "",
    val emailErrorMessage: String? = null,
    val password: String = "",
    val passwordErrorMessage: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordErrorMessage: String? = null,
    val isLoading: Boolean = false,
    val submitErrorMessage: String? = null
)