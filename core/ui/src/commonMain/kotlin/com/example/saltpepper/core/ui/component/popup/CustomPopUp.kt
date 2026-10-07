package com.example.saltpepper.core.ui.component.popup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.saltpepper.core.ui.component.button.CustomOutlinedButton
import com.example.saltpepper.core.ui.component.button.PrimaryButton
import com.example.saltpepper.core.ui.component.textfield.CustomOutlinedTextField
import com.example.saltpepper.core.ui.theme.SaltPepperTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomPopUp(
    modifier: Modifier = Modifier,
    popUpVariant: PopUpVariant = PopUpVariant.HORIZONTAL_BUTTONS,
    title: String,
    icon: ImageVector? = null,
    message: String,
    hasTextField: Boolean = false,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    dismissButtonText: String? = null,
    onDismiss: () -> Unit = {},
) {
    BasicAlertDialog(
        modifier = modifier
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(size = 12.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(size = 12.dp)
            )
            .padding(24.dp),
        onDismissRequest = onDismiss,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLargeEmphasized,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (hasTextField) {
                CustomOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = "",
                    onValueChange = { },
                    placeholder = "Placeholder"
                )
            }
            if (popUpVariant == PopUpVariant.HORIZONTAL_BUTTONS) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    dismissButtonText?.let {
                        CustomOutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = onDismiss,
                            text = dismissButtonText,
                            borderColor = MaterialTheme.colorScheme.outline
                        )
                    }
                    PrimaryButton(
                        modifier = Modifier.weight(1f),
                        onClick = onConfirm,
                        text = confirmButtonText,
                    )
                }
            } else {
                dismissButtonText?.let {
                    CustomOutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onDismiss,
                        text = dismissButtonText,
                        borderColor = MaterialTheme.colorScheme.outline
                    )
                }
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onConfirm,
                    text = confirmButtonText,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CustomPopUpPreview() {
    SaltPepperTheme {
        CustomPopUp(
            title = "Title",
            icon = Icons.Default.ErrorOutline,
            message = "Keep your messages short, but make sure they cover everything you need to say.",
            confirmButtonText = "Confirm",
            onConfirm = { },
            dismissButtonText = "Dismiss",
            onDismiss = { },
        )
    }
}

@Preview
@Composable
private fun CustomPopUpWithTextFieldPreview() {
    SaltPepperTheme {
        CustomPopUp(
            title = "Title",
            popUpVariant = PopUpVariant.VERTICAL_BUTTONS,
            message = "Keep your messages short, but make sure they cover everything you need to say.",
            confirmButtonText = "Confirm",
            onConfirm = { },
            hasTextField = true,
            dismissButtonText = "Dismiss",
            onDismiss = { },
        )
    }
}