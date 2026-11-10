package com.example.saltpepper.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dangerous
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.saltpepper.core.ui.component.button.ButtonVariant
import com.example.saltpepper.core.ui.component.button.CustomTextButton
import com.example.saltpepper.core.ui.component.popup.CustomPopUp
import com.example.saltpepper.core.ui.component.popup.PopUpVariant
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import com.example.saltpepper.profile.viewmodel.ProfileUiState
import org.jetbrains.compose.resources.stringResource
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.cancel_account_delete_button
import saltpepper.core.ui.generated.resources.delete_account_button
import saltpepper.core.ui.generated.resources.delete_account_code_sent
import saltpepper.core.ui.generated.resources.delete_account_popup_title
import saltpepper.core.ui.generated.resources.delete_account_warning
import saltpepper.core.ui.generated.resources.delete_created_recipes_label
import saltpepper.core.ui.generated.resources.no_code_received_helper_text
import saltpepper.core.ui.generated.resources.resend_code_button_text

@Composable
fun SendCodeToDeleteAccountPopUp(
    state: ProfileUiState,
    onDismissDeletion: () -> Unit,
    onRequestCode: () -> Unit,
    onConfirmDeletion: () -> Unit,
    onCodeChange: (String) -> Unit,
    onDeleteRecipesChange: (Boolean) -> Unit
) {
    CustomPopUp(
        popUpVariant = PopUpVariant.VERTICAL_BUTTONS,
        title = stringResource(Res.string.delete_account_popup_title),
        icon = Icons.Outlined.Dangerous,
        message = stringResource(
            if (state.codeSent) Res.string.delete_account_code_sent
            else Res.string.delete_account_warning),
        onDismiss = onDismissDeletion,
        dismissButtonText = stringResource(Res.string.cancel_account_delete_button),
        confirmButtonText = stringResource(Res.string.delete_account_button),
        hasTextField = state.codeSent,
        textFieldValue = state.code,
        onValueChange = onCodeChange,
        keyboardType = KeyboardType.Number,
        onConfirm = if (state.codeSent) onConfirmDeletion else onRequestCode,
        confirmEnabled = !state.codeSent || state.code.length == 6,
        isLoading = state.isLoading,
        isDestructive = true,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (state.codeSent) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(Res.string.no_code_received_helper_text),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        CustomTextButton(
                            buttonVariant = ButtonVariant.MEDIUM,
                            onClick = onRequestCode,
                            text = stringResource(Res.string.resend_code_button_text),
                            enabled = !state.isLoading
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.deleteCreatedRecipes,
                            onCheckedChange = onDeleteRecipesChange,
                            enabled = !state.isLoading
                        )
                        Text(
                            text = stringResource(Res.string.delete_created_recipes_label),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                state.errorMessage?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

@Preview
@Composable
private fun SendCodeToDeleteAccountPopUpCodeSentPreview() {
    SaltPepperTheme {
        SendCodeToDeleteAccountPopUp(
            state = ProfileUiState(showDeletionDialog = true, codeSent = true),
            onDismissDeletion = {},
            onRequestCode = {},
            onConfirmDeletion = {},
            onCodeChange = {},
            onDeleteRecipesChange = {}
        )
    }
}

@Preview
@Composable
private fun SendCodeToDeleteAccountPopUpCodeNotSentPreview() {
    SaltPepperTheme {
        SendCodeToDeleteAccountPopUp(
            state = ProfileUiState(showDeletionDialog = true, codeSent = false),
            onDismissDeletion = {},
            onRequestCode = {},
            onConfirmDeletion = {},
            onCodeChange = {},
            onDeleteRecipesChange = {}
        )
    }
}