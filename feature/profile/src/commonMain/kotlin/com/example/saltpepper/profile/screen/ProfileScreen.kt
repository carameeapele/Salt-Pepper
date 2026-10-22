package com.example.saltpepper.profile.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.saltpepper.core.ui.component.button.ButtonVariant
import com.example.saltpepper.core.ui.component.button.PrimaryButton
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import org.jetbrains.compose.resources.stringResource
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.logout_button_text
import saltpepper.core.ui.generated.resources.profile_empty_message
import saltpepper.core.ui.generated.resources.profile_screen_title

@Composable
fun ProfileScreen(
	modifier: Modifier = Modifier,
	onLogout: () -> Unit
) {
	Column(modifier = modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 32.dp)) {
		Text(
			text = stringResource(Res.string.profile_screen_title),
			style = MaterialTheme.typography.headlineLarge
		)
		Spacer(Modifier.height(16.dp))
		Text(
			text = stringResource(Res.string.profile_empty_message),
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
		Spacer(Modifier.height(24.dp))
		PrimaryButton(
            buttonVariant = ButtonVariant.MEDIUM,
            onClick = onLogout,
            text = stringResource(Res.string.logout_button_text)
        )
	}
}

@Preview
@Composable
private fun ProfileScreenPreview() {
	SaltPepperTheme {
		ProfileScreen(
			onLogout = {}
		)
	}
}