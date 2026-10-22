package com.example.saltpepper.authentication.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import org.jetbrains.compose.resources.stringResource
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.login_subtitle
import saltpepper.core.ui.generated.resources.login_title

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthHeadline(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 24.sp,
                maxFontSize = 48.sp,
                stepSize = 2.sp
            ),
            maxLines = 1,
            style = MaterialTheme.typography.displayLarge
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Preview
@Composable
fun AuthHeadlinePreview() {
    SaltPepperTheme {
        AuthHeadline(
            title = stringResource(Res.string.login_title),
            subtitle = stringResource(Res.string.login_subtitle)
        )
    }
}