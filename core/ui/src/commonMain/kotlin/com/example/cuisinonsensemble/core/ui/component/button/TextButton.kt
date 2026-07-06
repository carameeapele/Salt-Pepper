package com.example.cuisinonsensemble.core.ui.component.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TextButton(
    modifier: Modifier = Modifier,
    buttonVariant: ButtonVariant,
    onClick: () -> Unit,
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    leftIcon: ImageVector? = null,
    rightIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        modifier = modifier.height(buttonVariant.textOnlyHeight),
        onClick = onClick,
        shape = RoundedCornerShape(buttonVariant.cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = color
        ),
        contentPadding = PaddingValues(
            horizontal = 4.dp,
            vertical = 2.dp
        ),
        enabled = enabled
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (leftIcon != null) {
                Icon(
                    imageVector = leftIcon,
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
                    imageVector = rightIcon,
                    contentDescription = null,
                    modifier = Modifier.size(buttonVariant.iconSize)
                )
            }
        }
    }
}

@Preview
@Composable
fun TextButtonPreview() {
    TextButton(
        buttonVariant = ButtonVariant.GIANT,
        onClick = { },
        text = "Button"
    )
}