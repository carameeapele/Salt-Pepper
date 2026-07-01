package com.example.cuisinonsensemble.authentication.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuisinonsensemble.core.ui.component.button.PrimaryButton

private val PrimaryGreen = Color(0xff8CC63F)
private val TextGray = Color(0xff7293A0)

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
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PrimaryButton(
            modifier = modifier.fillMaxWidth(),
            onClick = onMainButtonClick,
            text = mainButtonText,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            enabled = !isLoading,
            isLoading = isLoading
        )

        Row {
            Text(
                text = helperText,
                color = TextGray,
                fontSize = 16.sp
            )

            TextButton(
                onClick = onRedirectionButtonClick,
                enabled = isLoading,
                colors = ButtonDefaults.buttonColors().copy(contentColor = PrimaryGreen),
            ) {
                Text(
                    text = redirectionButtonText
                )
            }
        }

    }
}

@Preview
@Composable
fun AuthBottomButtonsPreview() {
    AuthBottomButtons(
        mainButtonText = "Se connecter",
        onMainButtonClick = { },
        helperText = "Pas encore de compte ?",
        redirectionButtonText = "S'inscrire",
        onRedirectionButtonClick = { }
    )
}