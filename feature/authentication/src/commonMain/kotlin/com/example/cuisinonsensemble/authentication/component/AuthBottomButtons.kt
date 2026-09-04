package com.example.cuisinonsensemble.authentication.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuisinonsensemble.core.ui.component.button.ButtonVariant
import com.example.cuisinonsensemble.core.ui.component.button.PrimaryButton
import com.example.cuisinonsensemble.core.ui.component.button.CustomTextButton
import com.example.cuisinonsensemble.core.ui.theme.SaltPepperTheme
import cuisinonsensemble.core.ui.generated.resources.Res
import cuisinonsensemble.core.ui.generated.resources.login_button_text
import cuisinonsensemble.core.ui.generated.resources.no_account_helper_text
import cuisinonsensemble.core.ui.generated.resources.register_redirection_button_text
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthBottomButtons(
    modifier: Modifier = Modifier,
    mainButtonText: String,
    onMainButtonClick: () -> Unit,
    helperText: String,
    redirectionButtonText: String,
    onRedirectionButtonClick: () -> Unit,
    isLoading: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PrimaryButton(
            modifier = modifier.fillMaxWidth(),
            onClick = onMainButtonClick,
            text = mainButtonText,
            enabled = !isLoading,
            isLoading = isLoading,
            buttonVariant = ButtonVariant.LARGE
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = helperText,
                color = MaterialTheme.colorScheme.outline,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            CustomTextButton(
                buttonVariant = ButtonVariant.MEDIUM,
                onClick = onRedirectionButtonClick,
                text = redirectionButtonText,
                enabled = !isLoading
            )
        }

    }
}

@Preview
@Composable
fun AuthBottomButtonsPreview() {
    SaltPepperTheme {
        AuthBottomButtons(
            mainButtonText = stringResource(Res.string.login_button_text),
            onMainButtonClick = { },
            helperText = stringResource(Res.string.no_account_helper_text),
            redirectionButtonText = stringResource(Res.string.register_redirection_button_text),
            onRedirectionButtonClick = { }
        )
    }
}