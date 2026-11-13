package com.example.saltpepper.profile.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.saltpepper.core.ui.component.button.ButtonVariant
import com.example.saltpepper.core.ui.component.button.CustomOutlinedButton
import com.example.saltpepper.core.ui.component.button.PrimaryButton
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import com.example.saltpepper.profile.components.SendCodeToDeleteAccountPopUp
import com.example.saltpepper.profile.viewmodel.SettingsViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.delete_account_button
import saltpepper.core.ui.generated.resources.logout_button_text

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(state.accountDeleted) {
        if (state.accountDeleted) onAccountDeleted()
    }

    SettingsScreenContent(
        isLoading = isLoading,
        onLogout = onLogout,
        onDeleteClick = viewModel::openDeletionDialog
    )
    if (state.showDeletionDialog) {
        SendCodeToDeleteAccountPopUp(
            state = state,
            isLoading = isLoading,
            onDismissDeletion = viewModel::dismissDeletionDialog,
            onRequestCode = viewModel::requestDeletionCode,
            onConfirmDeletion = viewModel::deleteAccount,
            onCodeChange = viewModel::onCodeChange,
            onDeleteRecipesChange = viewModel::onDeleteCreatedRecipesChange
        )
    }
}

@Composable
private fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onLogout: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomOutlinedButton(
            modifier = modifier.fillMaxWidth(),
            buttonVariant = ButtonVariant.MEDIUM,
            text = stringResource(Res.string.logout_button_text),
            borderColor = MaterialTheme.colorScheme.error,
            onClick = onLogout,
            enabled = !isLoading
        )
        PrimaryButton(
            modifier = modifier.fillMaxWidth(),
            buttonVariant = ButtonVariant.MEDIUM,
            text = stringResource(Res.string.delete_account_button),
            containerColor = MaterialTheme.colorScheme.error,
            onClick = onDeleteClick,
            enabled = !isLoading
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SaltPepperTheme { SettingsScreenContent() }
}
