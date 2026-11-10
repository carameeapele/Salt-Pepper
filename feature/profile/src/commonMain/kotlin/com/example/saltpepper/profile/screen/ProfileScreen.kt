package com.example.saltpepper.profile.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.saltpepper.core.ui.component.button.CustomTextButton
import com.example.saltpepper.core.ui.component.button.PrimaryButton
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import com.example.saltpepper.profile.components.SendCodeToDeleteAccountPopUp
import com.example.saltpepper.profile.viewmodel.ProfileUiState
import com.example.saltpepper.profile.viewmodel.ProfileViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.delete_account_button
import saltpepper.core.ui.generated.resources.logout_button_text
import saltpepper.core.ui.generated.resources.profile_empty_message
import saltpepper.core.ui.generated.resources.profile_screen_title

@Composable
fun ProfileScreen(
	modifier: Modifier = Modifier,
	onLogout: () -> Unit,
    onAccountDeleted: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(state.accountDeleted) {
        if (state.accountDeleted) onAccountDeleted()
    }
    ProfileScreenContent(
        modifier = modifier,
        state = state,
        onLogout = onLogout,
        onDeleteClick = viewModel::openDeletionDialog,
        onDismissDeletion = viewModel::dismissDeletionDialog,
        onCodeChange = viewModel::onCodeChange,
        onDeleteRecipesChange = viewModel::onDeleteCreatedRecipesChange,
        onRequestCode = viewModel::requestDeletionCode,
        onConfirmDeletion = viewModel::deleteAccount
    )
}

@Composable
private fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    state: ProfileUiState = ProfileUiState(),
    onLogout: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onDismissDeletion: () -> Unit = {},
    onCodeChange: (String) -> Unit = {},
    onDeleteRecipesChange: (Boolean) -> Unit = {},
    onRequestCode: () -> Unit = {},
    onConfirmDeletion: () -> Unit = {}
) {
	Column(modifier = modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 32.dp)) {
		Text(
			text = stringResource(Res.string.profile_screen_title),
			style = MaterialTheme.typography.headlineLarge
		)
		Spacer(Modifier.height(16.dp))
		Text(
			text = stringResource(Res.string.profile_empty_message),
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		Spacer(Modifier.height(24.dp))
		PrimaryButton(
            buttonVariant = ButtonVariant.MEDIUM,
            onClick = onLogout,
            text = stringResource(Res.string.logout_button_text),
            enabled = !state.isLoading
        )
        Spacer(Modifier.height(16.dp))
        CustomTextButton(
            buttonVariant = ButtonVariant.MEDIUM,
            onClick = onDeleteClick,
            text = stringResource(Res.string.delete_account_button),
            color = MaterialTheme.colorScheme.error,
            enabled = !state.isLoading
        )
	}
    if (state.showDeletionDialog) {
        SendCodeToDeleteAccountPopUp(
            state = state,
            onDismissDeletion = onDismissDeletion,
            onRequestCode = onRequestCode,
            onConfirmDeletion = onConfirmDeletion,
            onCodeChange = onCodeChange,
            onDeleteRecipesChange = onDeleteRecipesChange
        )
    }

}

@Preview
@Composable
private fun ProfileScreenPreview() {
	SaltPepperTheme {
		ProfileScreenContent(
			onLogout = {}
		)
	}
}