package com.example.saltpepper.core.ui.component.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.login_button_text
import org.jetbrains.compose.resources.stringResource

@Composable
fun CustomOutlinedButton(
    modifier: Modifier = Modifier,
    buttonVariant: ButtonVariant = ButtonVariant.MEDIUM,
    onClick: () -> Unit,
    text: String,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    leftIcon: Painter? = null,
    rightIcon: Painter? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(buttonVariant.cornerRadius),
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        ),
        contentPadding = PaddingValues(
            horizontal = buttonVariant.horizontalPadding,
            vertical = buttonVariant.verticalPadding
        ),
        enabled = enabled
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(buttonVariant.iconSize),
                color = borderColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leftIcon != null) {
                    Icon(
                        modifier = Modifier.size(buttonVariant.iconSize),
                        tint = borderColor,
                        painter = leftIcon,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = borderColor,
                    fontSize = buttonVariant.textSize,
                    fontWeight = FontWeight.SemiBold
                )
                if (rightIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        modifier = Modifier.size(buttonVariant.iconSize),
                        tint = borderColor,
                        painter = rightIcon,
                        contentDescription = null
                    )
                }
            }

        }
    }
}

@Preview
@Composable
private fun CustomOutlinedButtonPreview() {
    SaltPepperTheme {
        CustomOutlinedButton(
            onClick = { },
            text = stringResource(Res.string.login_button_text),
            buttonVariant = ButtonVariant.GIANT
        )
    }
}