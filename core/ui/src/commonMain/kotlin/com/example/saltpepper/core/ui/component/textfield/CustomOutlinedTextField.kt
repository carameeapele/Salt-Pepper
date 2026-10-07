package com.example.saltpepper.core.ui.component.textfield

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.email_label
import saltpepper.core.ui.generated.resources.email_placeholder
import saltpepper.core.ui.generated.resources.password_label
import saltpepper.core.ui.generated.resources.password_placeholder
import org.jetbrains.compose.resources.stringResource

@Composable
fun CustomOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    visibilityToggle: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    helperText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true
) {
    var textVisibility by remember { mutableStateOf(false) }

    val visualTransformation = when {
        visibilityToggle && !textVisibility -> PasswordVisualTransformation()
        else -> VisualTransformation.None
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (label != null) {
            Text(
                text = label,
                color = if (isError)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.outline,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        OutlinedTextField(
            modifier = modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                if (placeholder != null) {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 16.sp
                    )
                }
            },
            leadingIcon = if (leadingIcon != null) {{
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )}
            } else null,
            trailingIcon = when {
                visibilityToggle -> {
                    {
                        IconButton(onClick = { textVisibility = !textVisibility }) {
                            Icon(
                                imageVector = if (textVisibility)
                                    Icons.Outlined.VisibilityOff
                                else
                                    Icons.Outlined.Visibility,
                                contentDescription = if (textVisibility) "Masquer" else "Afficher",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                trailingIcon != null -> {
                    {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                else -> null
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLeadingIconColor = MaterialTheme.colorScheme.error
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = visualTransformation,
            enabled = enabled,
            isError = isError,
            singleLine = true
        )

        if (helperText != null) {
            Text(
                text = helperText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview
@Composable
fun CustomOutlinedTextFieldPreview() {
    SaltPepperTheme {
        CustomOutlinedTextField(
            value = "",
            label = stringResource(Res.string.email_label),
            onValueChange = { },
            placeholder = stringResource(Res.string.email_placeholder),
            leadingIcon = Icons.Outlined.Email,
            visibilityToggle = false,
            keyboardType = KeyboardType.Email,
            enabled = true
        )
    }
}

@Preview
@Composable
fun CustomOutlinedTextFieldPreviewWithVisibilityToggle() {
    SaltPepperTheme {
        CustomOutlinedTextField(
            value = "",
            label = stringResource(Res.string.password_label),
            onValueChange = { },
            placeholder = stringResource(Res.string.password_placeholder),
            leadingIcon = Icons.Outlined.Lock,
            visibilityToggle = true,
            keyboardType = KeyboardType.Password,
            helperText = "This field is mandatory",
            enabled = true,
            isError = true
        )
    }
}