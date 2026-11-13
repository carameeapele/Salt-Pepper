package com.example.saltpepper.profile.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.saltpepper.core.ui.viewmodel.BaseViewModel
import com.example.saltpepper.data.profile.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val showDeletionDialog: Boolean = false,
    val codeSent: Boolean = false,
    val code: String = "",
    val deleteCreatedRecipes: Boolean = false,
    val errorMessage: String? = null,
    val accountDeleted: Boolean = false
)

class SettingsViewModel(
    private val profileRepository: ProfileRepository
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    fun openDeletionDialog() {
        if (!mIsLoading.value) {
            _uiState.update { it.copy(showDeletionDialog = true) }
        }
    }

    fun dismissDeletionDialog() {
        if (!mIsLoading.value) {
            _uiState.update {
                it.copy(showDeletionDialog = false, codeSent = false, code = "",
                    deleteCreatedRecipes = false, errorMessage = null)
            }
        }
    }

    fun onCodeChange(value: String) {
        if (mIsLoading.value) return
        _uiState.update { it.copy(code = value.filter(Char::isDigit).take(6), errorMessage = null) }
    }

    fun onDeleteCreatedRecipesChange(value: Boolean) {
        if (mIsLoading.value) return
        _uiState.update { it.copy(deleteCreatedRecipes = value) }
    }

    fun requestDeletionCode() {
        val state = _uiState.value
        if (mIsLoading.value || !state.showDeletionDialog) return
        mIsLoading.value = true
        _uiState.update { it.copy(errorMessage = null) }
        viewModelScope.launch {
            profileRepository.requestAccountDeletionCode().fold(
                onSuccess = {
                    mIsLoading.value = false
                    _uiState.update { it.copy(codeSent = true, code = "") }
                },
                onFailure = { error ->
                    mIsLoading.value = false
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "Could not send deletion code")
                    }
                }
            )
        }
    }

    fun deleteAccount() {
        val state = _uiState.value
        if (mIsLoading.value || !state.showDeletionDialog || !state.codeSent || state.code.length != 6) return
        mIsLoading.value = true
        _uiState.update { it.copy(errorMessage = null) }
        viewModelScope.launch {
            profileRepository.deleteAccount(state.code, state.deleteCreatedRecipes).fold(
                onSuccess = {
                    mIsLoading.value = false
                    _uiState.value = SettingsUiState(accountDeleted = true)
                },
                onFailure = { error ->
                    mIsLoading.value = false
                    _uiState.update {
                        it.copy(errorMessage = error.message ?: "Account deletion failed")
                    }
                }
            )
        }
    }
}
