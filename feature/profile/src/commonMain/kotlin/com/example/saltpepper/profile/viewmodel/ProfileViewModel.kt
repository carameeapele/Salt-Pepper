package com.example.saltpepper.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saltpepper.data.model.MemberProfile
import com.example.saltpepper.data.model.ProfileRecipe
import com.example.saltpepper.data.repository.AuthRepository
import com.example.saltpepper.data.repository.ProfileRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ProfileScreenUiState {
    data object Loading : ProfileScreenUiState
    data class Success(
        val profile: MemberProfile,
        val favorites: List<ProfileRecipe>,
        val myRecipes: List<ProfileRecipe>
    ) : ProfileScreenUiState
    data class Error(val message: String) : ProfileScreenUiState
}

data class ProfileUiState(
    val screenState: ProfileScreenUiState = ProfileScreenUiState.Loading,
    val showDeletionDialog: Boolean = false,
    val codeSent: Boolean = false,
    val code: String = "",
    val deleteCreatedRecipes: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val accountDeleted: Boolean = false
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()
    private var refreshJob: Job? = null

    fun refresh() {
        if (refreshJob?.isActive == true || _uiState.value.accountDeleted) return
        _uiState.update { it.copy(screenState = ProfileScreenUiState.Loading) }
        refreshJob = viewModelScope.launch {
            val profileRequest = async { profileRepository.getProfile() }
            val favoritesRequest = async { profileRepository.getFavorites() }
            val recipesRequest = async { profileRepository.getMyRecipes() }
            val profile = profileRequest.await()
            val favorites = favoritesRequest.await()
            val recipes = recipesRequest.await()
            val errors = listOfNotNull(
                profile.exceptionOrNull()?.let { it.message ?: "Could not load profile" },
                favorites.exceptionOrNull()?.let { it.message ?: "Could not load favorites" },
                recipes.exceptionOrNull()?.let { it.message ?: "Could not load recipes" }
            )
            val screenState = if (errors.isEmpty()) {
                ProfileScreenUiState.Success(
                    profile.getOrThrow(), favorites.getOrThrow(), recipes.getOrThrow()
                )
            } else {
                ProfileScreenUiState.Error(errors.distinct().joinToString("\n"))
            }
            _uiState.update { if (it.accountDeleted) it else it.copy(screenState = screenState) }
        }
    }

    fun openDeletionDialog() {
        if (!_uiState.value.isLoading) {
            _uiState.update { it.copy(showDeletionDialog = true) }
        }
    }

    fun dismissDeletionDialog() {
        if (!_uiState.value.isLoading) {
            _uiState.update {
                it.copy(showDeletionDialog = false, codeSent = false, code = "",
                    deleteCreatedRecipes = false, errorMessage = null)
            }
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
                    refreshJob?.cancel()
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
