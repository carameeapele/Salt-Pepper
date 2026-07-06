package com.example.cuisinonsensemble.core.ui.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    buttonVariant: ButtonVariant,
    onClick: () -> Unit,
    text: String,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = Color.White,
    leftIcon: Painter? = null,
    rightIcon: Painter? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(buttonVariant.cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        enabled = enabled
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(buttonVariant.iconSize),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leftIcon != null) {
                    Icon(
                        painter = leftIcon,
                        contentDescription = null,
                        modifier = Modifier.size(buttonVariant.iconSize)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontSize = buttonVariant.textSize,
                    fontWeight = FontWeight.SemiBold
                )
                if (rightIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = rightIcon,
                        contentDescription = null,
                        modifier = Modifier.size(buttonVariant.iconSize)
                    )
                }
            }

        }
    }
}

@Preview
@Composable
fun PrimaryButtonPreview() {
    PrimaryButton(
        onClick = { },
        text = "Se connecter",
        buttonVariant = ButtonVariant.GIANT
    )
}