package com.example.saltpepper.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saltpepper.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val showDeletionDialog: Boolean = false,
    val codeSent: Boolean = false,
    val code: String = "",
    val deleteCreatedRecipes: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val accountDeleted: Boolean = false
)

class ProfileViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun openDeletionDialog() {
        if (!_uiState.value.isLoading) {
            _uiState.update { it.copy(showDeletionDialog = true) }
        }
    }

    fun dismissDeletionDialog() {
        if (!_uiState.value.isLoading) {
            _uiState.value = ProfileUiState()
        }
    }

    fun onCodeChange(value: String) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(code = value.filter(Char::isDigit).take(6), errorMessage = null) }
    }

    fun onDeleteCreatedRecipesChange(value: Boolean) {
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(deleteCreatedRecipes = value) }
    }

    fun requestDeletionCode() {
        val state = _uiState.value
        if (state.isLoading || !state.showDeletionDialog) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            authRepository.requestAccountDeletionCode().fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, codeSent = true, code = "") }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Could not send deletion code")
                    }
                }
            )
        }
    }

    fun deleteAccount() {
        val state = _uiState.value
        if (state.isLoading || !state.showDeletionDialog || !state.codeSent || state.code.length != 6) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            authRepository.deleteAccount(state.code, state.deleteCreatedRecipes).fold(
                onSuccess = {
                    _uiState.value = ProfileUiState(accountDeleted = true)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Account deletion failed")
                    }
                }
            )
        }
    }
}
