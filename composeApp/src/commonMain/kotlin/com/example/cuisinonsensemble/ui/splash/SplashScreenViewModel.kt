package com.example.cuisinonsensemble.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface SplashScreenUiState {
    data object Loading : SplashScreenUiState
    data object Success : SplashScreenUiState
}

class SplashScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<SplashScreenUiState>(SplashScreenUiState.Loading)
    val uiState: StateFlow<SplashScreenUiState> = _uiState

    init {
        viewModelScope.launch {
            delay(1500)
            _uiState.value = SplashScreenUiState.Success
        }
    }
}