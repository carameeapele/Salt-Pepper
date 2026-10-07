package com.example.saltpepper.authentication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saltpepper.authentication.model.RegisterScreenUiModel
import com.example.saltpepper.data.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterScreenViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiModel = MutableStateFlow(RegisterScreenUiModel())
    val uiModel: StateFlow<RegisterScreenUiModel> = _uiModel.asStateFlow()

    private val _events = Channel<RegisterScreenEvent>(Channel.BUFFERED)
    val events: Flow<RegisterScreenEvent> = _events.receiveAsFlow()

    private companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private val EMAIL_REGEX =
            Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }

    private enum class Field {
        EMAIL,
        PASSWORD,
        CONFIRM_PASSWORD
    }

    private data class ValidationError(
        val field: Field,
        val message: String
    )

    fun onNameChange(value: String) {
        _uiModel.update { it.copy(name = value) }
    }

    fun onEmailChange(value: String) {
        _uiModel.update { it.copy(email = value, emailErrorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiModel.update { it.copy(password = value, passwordErrorMessage = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiModel.update { it.copy(confirmPassword = value, confirmPasswordErrorMessage = null) }
    }

    fun dismissSubmitError() {
        _uiModel.update { it.copy(submitErrorMessage = null) }
    }

    fun register() {
        val currentModel = _uiModel.value
        if (currentModel.isLoading) return

        val validationError = validateInput(currentModel)
        if (validationError != null) {
            _uiModel.update {
                it.copy(
                    emailErrorMessage = validationError.messageFor(Field.EMAIL),
                    passwordErrorMessage = validationError.messageFor(Field.PASSWORD),
                    confirmPasswordErrorMessage = validationError.messageFor(Field.CONFIRM_PASSWORD),
                    submitErrorMessage = null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiModel.update { it.copy(isLoading = true, submitErrorMessage = null) }

            val result = authRepository.register(currentModel.email.trim(), currentModel.password)

            result.fold(
                onSuccess = {
                    _uiModel.update { it.copy(isLoading = false) }
                    _events.send(RegisterScreenEvent.RegisterSucceeded)
                },
                onFailure = {
                    _uiModel.update {
                        it.copy(
                            isLoading = false,
                            submitErrorMessage = "The service is currently unavailable. Please try again later."
                        )
                    }
                }
            )
        }
    }

    private fun ValidationError.messageFor(target: Field): String? =
        message.takeIf { field == target }

    private fun validateInput(state: RegisterScreenUiModel): ValidationError? {
        val email = state.email.trim()
        val password = state.password

        if (email.isBlank()) return ValidationError(Field.EMAIL, "Email is required")
        if (!EMAIL_REGEX.matches(email)) {
            return ValidationError(
                Field.EMAIL,
                "Please enter a valid email address (example@domain.com)"
            )
        }
        if (password.isBlank()) return ValidationError(Field.PASSWORD, "Password is required")
        if (password.length < MIN_PASSWORD_LENGTH) {
            return ValidationError(
                Field.PASSWORD,
                "Password must be at least $MIN_PASSWORD_LENGTH characters"
            )
        }
        if (password.none { it.isUpperCase() }) {
            return ValidationError(
                Field.PASSWORD,
                "Password must include at least one uppercase letter"
            )
        }
        if (password.none { it.isLowerCase() }) {
            return ValidationError(
                Field.PASSWORD,
                "Password must include at least one lowercase letter"
            )
        }
        if (password.none { it.isDigit() }) {
            return ValidationError(
                Field.PASSWORD,
                "Password must include at least one number"
            )
        }
        if (state.confirmPassword.isBlank()) {
            return ValidationError(Field.CONFIRM_PASSWORD, "Please confirm your password")
        }
        if (password != state.confirmPassword) {
            return ValidationError(Field.CONFIRM_PASSWORD, "Passwords must match")
        }

        return null
    }
}

sealed interface RegisterScreenEvent {
    data object RegisterSucceeded : RegisterScreenEvent
}