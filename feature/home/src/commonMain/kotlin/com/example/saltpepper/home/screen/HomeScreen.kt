package com.example.saltpepper.home.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.browse_recipes_action
import saltpepper.core.ui.generated.resources.home_welcome_subtitle
import saltpepper.core.ui.generated.resources.home_welcome_title
import saltpepper.core.ui.generated.resources.plan_meals_action

@Composable
fun HomeScreen(
    onNavigateToBrowseRecipes: () -> Unit,
    onPlanMeals: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        HomeContent(
            onNavigateToBrowseRecipes = onNavigateToBrowseRecipes,
            onPlanMeals = onPlanMeals
        )
    }
}

@Composable
private fun HomeContent(
    onNavigateToBrowseRecipes: () -> Unit,
    onPlanMeals: () -> Unit
) {
    Text(
        text = stringResource(Res.string.home_welcome_title),
        style = MaterialTheme.typography.headlineLarge
    )
    Spacer(Modifier.height(12.dp))
    Text(
        text = stringResource(Res.string.home_welcome_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(36.dp))
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onNavigateToBrowseRecipes) {
        Text(stringResource(Res.string.browse_recipes_action))
    }
    Spacer(Modifier.height(12.dp))
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onPlanMeals
    ) {
        Text(stringResource(Res.string.plan_meals_action))
    }
}
