package com.example.saltpepper.authentication.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.login_subtitle
import saltpepper.core.ui.generated.resources.login_title
import org.jetbrains.compose.resources.stringResource

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
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = subtitle,
            fontSize = 16.sp,
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