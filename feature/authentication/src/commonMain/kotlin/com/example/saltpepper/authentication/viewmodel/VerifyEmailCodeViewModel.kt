package com.example.saltpepper.authentication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saltpepper.data.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VerifyEmailCodeUiState(
    val code: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isVerified: Boolean = false
)

class VerifyEmailCodeViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(VerifyEmailCodeUiState())
    val uiState: StateFlow<VerifyEmailCodeUiState> = _uiState.asStateFlow()

    fun onCodeChange(value: String) {
        _uiState.update {
            it.copy(code = value.filter(Char::isDigit).take(6), errorMessage = null)
        }
    }

    fun verify(email: String) {
        val code = _uiState.value.code
        if (_uiState.value.isLoading) return
        if (code.length != 6) {
            _uiState.update { it.copy(errorMessage = "Enter all six digits") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                authRepository.verifyEmailCode(email, code).getOrThrow()
                _uiState.update { it.copy(isLoading = false, isVerified = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Verification failed")
                }
            }
        }
    }

    fun resend(email: String) {
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
}