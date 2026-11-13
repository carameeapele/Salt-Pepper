package com.example.saltpepper.core.ui.model

data class DialogUiModel(
    val isShowing: Boolean = false,
    val title: String? = null,
    val message: String = "",
    val confirmButtonText: String = "",
    val cancelButtonText: String? = null,
    val onConfirmClick: () -> Unit = {},
    val onCancelClick: () -> Unit = {},
    val onDismiss: () -> Unit = {}
)