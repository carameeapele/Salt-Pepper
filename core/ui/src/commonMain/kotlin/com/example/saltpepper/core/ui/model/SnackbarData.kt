package com.example.saltpepper.core.ui.model

sealed interface SnackbarUiState {
    val message: String
    val textButton: String?
    val actionButton: () -> Unit
    data class ShortSnackbar(
        override val message: String,
        override val textButton: String? = null,
        override val actionButton: () -> Unit = {}
    ) : SnackbarUiState
    data class LongSnackbar(
        override val message: String,
        override val textButton: String? = null,
        override val actionButton: () -> Unit = {}
    ) : SnackbarUiState
}