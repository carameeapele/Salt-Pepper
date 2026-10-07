package com.example.cuisinonsensemble.authentication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.cuisinonsensemble.data.repository.AuthRepository
import kotlinx.coroutines.CancellationException

data class LoginScreenUiState(
    val email: String = "",
    val code: String = "",
    val codeSent: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)

class LoginScreenViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginScreenUiState())
    val uiState: StateFlow<LoginScreenUiState> = _uiState

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, code = "", codeSent = false, errorMessage = null) }
    }

    fun onCodeChange(value: String) {
        _uiState.update { it.copy(code = value.filter(Char::isDigit), errorMessage = null) }
    }

    fun login() {
        val state = _uiState.value
        if (state.isLoading) return
        if (state.email.isBlank() || !EMAIL_REGEX.matches(state.email.trim())) {
            _uiState.update { it.copy(errorMessage = "Enter a valid email address") }
            return
        }
        if (state.codeSent && state.code.length != 6) {
            _uiState.update { it.copy(errorMessage = "Enter all six digits") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                if (state.codeSent) {
                    authRepository.verifyEmailCode(state.email.trim(), state.code).getOrThrow()
                    _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
                } else {
                    authRepository.requestEmailCode(state.email.trim()).getOrThrow()
                    _uiState.update { it.copy(isLoading = false, codeSent = true) }
                }
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

    fun backToEmail() {
        _uiState.update { it.copy(codeSent = false, code = "", errorMessage = null) }
    }

    fun resendCode() {
        val email = _uiState.value.email.trim()
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                authRepository.requestEmailCode(email).getOrThrow()
                _uiState.update { it.copy(isLoading = false, code = "") }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Could not resend code")
                }
            }
        }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}