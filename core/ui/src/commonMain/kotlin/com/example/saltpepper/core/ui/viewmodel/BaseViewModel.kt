package com.example.saltpepper.core.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.saltpepper.core.ui.model.DialogUiModel
import com.example.saltpepper.core.ui.model.SnackbarUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel : ViewModel() {
    protected val mMessage = MutableSharedFlow<SnackbarUiState>()
    val message = mMessage.asSharedFlow()

    protected val mIsLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = mIsLoading

    protected val mDialogUiModel = MutableStateFlow(DialogUiModel())
    val dialogUiModel = mDialogUiModel.asStateFlow()
}