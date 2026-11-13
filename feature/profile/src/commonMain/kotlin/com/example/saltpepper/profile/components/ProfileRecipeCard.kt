package com.example.saltpepper.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.saltpepper.data.recipe.model.Recipe
import org.jetbrains.compose.resources.stringResource
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.profile_recipe_minutes

@Composable
fun ProfileRecipeCard(recipe: Recipe, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(recipe.title, style = MaterialTheme.typography.titleMedium)
            recipe.totalTimeMinutes?.takeIf { it > 0 }?.let {
                Text(
                    stringResource(Res.string.profile_recipe_minutes, it),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
