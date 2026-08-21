package com.example.cuisinonsensemble.authentication.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cuisinonsensemble.core.ui.theme.SaltPepperTheme

@Composable
fun AuthHeadline(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            fontSize = 48.sp,
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
            title = "Bon retour",
            subtitle = "Connectez-vous pour continuer"
        )
    }
}