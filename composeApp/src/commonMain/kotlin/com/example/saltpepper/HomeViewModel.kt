package com.example.saltpepper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow("Hello KMP")
    val uiState: StateFlow<String> = _uiState

    fun updateMessage(newMessage: String) {
        viewModelScope.launch {
            _uiState.update { newMessage }
        }
    }
}