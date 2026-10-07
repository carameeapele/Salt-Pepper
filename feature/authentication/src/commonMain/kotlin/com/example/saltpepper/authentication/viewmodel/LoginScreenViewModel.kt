package com.example.saltpepper.authentication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.saltpepper.data.repository.AuthRepository
import kotlinx.coroutines.CancellationException

data class LoginScreenUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emailCodeRequested: Boolean = false
)

class LoginScreenViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginScreenUiState())
    val uiState: StateFlow<LoginScreenUiState> = _uiState

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
    }

    fun sendEmailCode() {
        val state = _uiState.value
        if (state.isLoading) return
        if (state.email.isBlank() || !EMAIL_REGEX.matches(state.email.trim())) {
            _uiState.update { it.copy(errorMessage = "Enter a valid email address") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                authRepository.requestEmailCode(state.email.trim()).getOrThrow()
                _uiState.update { it.copy(isLoading = false, emailCodeRequested = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Login failed")
                }
            }
        }
    }

    fun consumeErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun consumeEmailCodeRequested() {
        _uiState.update { it.copy(emailCodeRequested = false) }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}