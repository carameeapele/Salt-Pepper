package com.example.saltpepper.profile.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.saltpepper.core.ui.component.button.ButtonVariant
import com.example.saltpepper.core.ui.component.button.CustomOutlinedButton
import com.example.saltpepper.core.ui.component.button.CustomTextButton
import com.example.saltpepper.core.ui.component.button.PrimaryButton
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import com.example.saltpepper.profile.components.SendCodeToDeleteAccountPopUp
import com.example.saltpepper.profile.viewmodel.ProfileUiState
import com.example.saltpepper.profile.viewmodel.ProfileViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.back
import saltpepper.core.ui.generated.resources.delete_account_button
import saltpepper.core.ui.generated.resources.logout_button_text
import saltpepper.core.ui.generated.resources.settings_title

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.accountDeleted) {
        if (state.accountDeleted) onAccountDeleted()
    }

    SettingsScreenContent(
        state = state,
        onBack = onBack,
        onLogout = onLogout,
        onDeleteClick = viewModel::openDeletionDialog
    )
    if (state.showDeletionDialog) {
        SendCodeToDeleteAccountPopUp(
            state = state,
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
    state: ProfileUiState = ProfileUiState(),
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CustomTextButton(
            buttonVariant = ButtonVariant.MEDIUM,
            text = stringResource(Res.string.back),
            leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onClick = onBack,
            enabled = !state.isLoading
        )
        Text(
            text = stringResource(Res.string.settings_title),
            style = MaterialTheme.typography.headlineLarge
        )
        CustomOutlinedButton(
            modifier = modifier.fillMaxWidth(),
            buttonVariant = ButtonVariant.MEDIUM,
            text = stringResource(Res.string.logout_button_text),
            borderColor = MaterialTheme.colorScheme.error,
            onClick = onLogout,
            enabled = !state.isLoading
        )
        PrimaryButton(
            modifier = modifier.fillMaxWidth(),
            buttonVariant = ButtonVariant.MEDIUM,
            text = stringResource(Res.string.delete_account_button),
            containerColor = MaterialTheme.colorScheme.error,
            onClick = onDeleteClick,
            enabled = !state.isLoading
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SaltPepperTheme { SettingsScreenContent() }
}
