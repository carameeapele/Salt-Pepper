package com.example.cuisinonsensemble.authentication.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuisinonsensemble.authentication.component.AuthHeadline
import com.example.cuisinonsensemble.core.ui.component.button.ButtonVariant
import com.example.cuisinonsensemble.core.ui.component.button.CustomTextButton
import com.example.cuisinonsensemble.core.ui.component.button.PrimaryButton
import com.example.cuisinonsensemble.core.ui.theme.SaltPepperTheme
import cuisinonsensemble.core.ui.generated.resources.Res
import cuisinonsensemble.core.ui.generated.resources.back
import cuisinonsensemble.core.ui.generated.resources.code_sent_subtitle
import cuisinonsensemble.core.ui.generated.resources.login_title
import cuisinonsensemble.core.ui.generated.resources.send_code_button_text
import cuisinonsensemble.core.ui.generated.resources.verify_code_button_text
import cuisinonsensemble.core.ui.generated.resources.verification_code_label
import cuisinonsensemble.core.ui.generated.resources.change_email_button_text
import org.jetbrains.compose.resources.stringResource

@Composable
fun VerifyEmailCodeScreen(
    email: String,
    code: String,
    isLoading: Boolean,
    errorMessage: String?,
    onCodeChange: (String) -> Unit,
    onVerify: () -> Unit,
    onResendCode: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = FocusRequester()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 60.dp)
    ) {
        CustomTextButton(
            buttonVariant = ButtonVariant.MEDIUM,
            onClick = onBack,
            text = stringResource(Res.string.back),
            leftIcon = Icons.AutoMirrored.Filled.ArrowBack
        )

        Spacer(Modifier.height(46.dp))

        AuthHeadline(
            title = stringResource(Res.string.login_title),
            subtitle = stringResource(Res.string.code_sent_subtitle)
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(44.dp))

        Text(
            text = stringResource(Res.string.verification_code_label),
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(Modifier.height(12.dp))

        Box {
            BasicTextField(
                value = code,
                onValueChange = { value ->
                    onCodeChange(value.filter(Char::isDigit).take(6))
                },
                modifier = Modifier
                    .size(1.dp)
                    .focusRequester(focusRequester),
                enabled = !isLoading,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isLoading) { focusRequester.requestFocus() },
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(6) { index ->
                    val digit = code.getOrNull(index)?.toString().orEmpty()
                    val isActive = index == code.length && code.length < 6
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    BorderStroke(
                                        if (isActive || errorMessage != null) 2.dp else 1.dp,
                                        when {
                                            errorMessage != null -> MaterialTheme.colorScheme.error
                                            isActive -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.outline
                                        }
                                    ),
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = digit,
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 24.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.weight(1f))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = onResendCode, enabled = !isLoading) {
                    Text(stringResource(Res.string.send_code_button_text))
                }
                TextButton(onClick = onBack, enabled = !isLoading) {
                    Text(stringResource(Res.string.change_email_button_text))
                }
            }
            Spacer(Modifier.height(8.dp))
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onVerify,
                text = stringResource(Res.string.verify_code_button_text),
                enabled = !isLoading && code.length == 6,
                isLoading = isLoading,
                buttonVariant = ButtonVariant.LARGE
            )
        }
    }
}

@Preview
@Composable
private fun VerifyEmailCodeScreenPreview() {
    SaltPepperTheme {
        VerifyEmailCodeScreen(
            email = "user@example.com",
            code = "527",
            isLoading = false,
            errorMessage = null,
            onCodeChange = {},
            onVerify = {},
            onResendCode = {},
            onBack = {}
        )
    }
}