package com.example.saltpepper.authentication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saltpepper.data.authentication.repository.AuthRepository
import com.example.saltpepper.data.profile.repository.ProfileRepository
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
    val isRegistrationVerified: Boolean = false,
    val isVerified: Boolean = false
)

class VerifyEmailCodeViewModel(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(VerifyEmailCodeUiState())
    val uiState: StateFlow<VerifyEmailCodeUiState> = _uiState.asStateFlow()

    fun onCodeChange(value: String) {
        _uiState.update {
            it.copy(code = value.filter(Char::isDigit).take(6), errorMessage = null)
        }
    }

    fun verify(email: String, isRegistration: Boolean = false, displayName: String = "") {
        val state = _uiState.value
        if (state.isLoading) return
        if (!(isRegistration && state.isRegistrationVerified) && state.code.length != 6) {
            _uiState.update { it.copy(errorMessage = "Enter all six digits") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                if (isRegistration) {
                    if (!state.isRegistrationVerified) {
                        authRepository.verifyRegistrationCode(email, state.code).getOrThrow()
                        _uiState.update { it.copy(isRegistrationVerified = true) }
                    }
                    profileRepository.updateDisplayName(displayName.trim()).getOrThrow()
                } else {
                    authRepository.verifyEmailCode(email, state.code).getOrThrow()
                }
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