package com.example.saltpepper.recipes.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.recipes_empty_message
import saltpepper.core.ui.generated.resources.recipes_screen_title

@Composable
fun RecipesScreen(modifier: Modifier = Modifier) {
	Column(modifier = modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 32.dp)) {
		Text(stringResource(Res.string.recipes_screen_title), style = MaterialTheme.typography.headlineLarge)
		Spacer(Modifier.height(16.dp))
		Text(
			text = stringResource(Res.string.recipes_empty_message),
			style = MaterialTheme.typography.bodyLarge,
			color = MaterialTheme.colorScheme.onSurfaceVariant
		)
	}
}