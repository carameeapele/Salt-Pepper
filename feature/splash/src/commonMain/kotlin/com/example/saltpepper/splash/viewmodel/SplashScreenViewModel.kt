package com.example.saltpepper.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saltpepper.data.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface SplashScreenUiState {
    data object Loading : SplashScreenUiState
    data class Success(val authenticated: Boolean) : SplashScreenUiState
    data object Error : SplashScreenUiState
}

class SplashScreenViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<SplashScreenUiState>(SplashScreenUiState.Loading)
    val uiState: StateFlow<SplashScreenUiState> = _uiState

    init {
        restoreSession(initialDelay = true)
    }

    fun retry() {
        if (_uiState.value == SplashScreenUiState.Error) {
            restoreSession()
        }
    }

    private fun restoreSession(initialDelay: Boolean = false) {
        _uiState.value = SplashScreenUiState.Loading
        viewModelScope.launch {
            if (initialDelay) delay(1500)
            _uiState.value = authRepository.restoreSession().fold(
                onSuccess = { user -> SplashScreenUiState.Success(authenticated = user != null) },
                onFailure = { SplashScreenUiState.Error }
            )
        }
    }
}