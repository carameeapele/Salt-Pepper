package com.example.saltpepper.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saltpepper.data.model.ProfileRecipe
import com.example.saltpepper.data.repository.ProfileRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState
    data class Success(val recipes: List<ProfileRecipe>) : FavoritesUiState
    data class Error(val message: String) : FavoritesUiState
}

class FavoritesViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private var refreshJob: Job? = null

    fun refresh() {
        if (refreshJob?.isActive == true) return
        _uiState.value = FavoritesUiState.Loading
        refreshJob = viewModelScope.launch {
            _uiState.value = repository.getFavorites().fold(
                onSuccess = { FavoritesUiState.Success(it) },
                onFailure = { FavoritesUiState.Error(it.message ?: "Could not load favorites") }
            )
        }
    }
}
