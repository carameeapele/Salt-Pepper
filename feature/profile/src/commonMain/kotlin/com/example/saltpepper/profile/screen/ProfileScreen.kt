package com.example.saltpepper.profile.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.saltpepper.core.ui.component.button.PrimaryButton
import com.example.saltpepper.core.ui.component.screen.ErrorScreen
import com.example.saltpepper.core.ui.component.screen.LoadingScreen
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import com.example.saltpepper.data.model.MemberProfile
import com.example.saltpepper.data.model.ProfileRecipe
import com.example.saltpepper.profile.util.formatDate
import com.example.saltpepper.profile.viewmodel.ProfileScreenUiState
import com.example.saltpepper.profile.viewmodel.ProfileUiState
import com.example.saltpepper.profile.viewmodel.ProfileViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.browse_recipes_action
import saltpepper.core.ui.generated.resources.profile_creation_coming_soon
import saltpepper.core.ui.generated.resources.profile_favorites
import saltpepper.core.ui.generated.resources.profile_import_recipe
import saltpepper.core.ui.generated.resources.profile_member_since
import saltpepper.core.ui.generated.resources.profile_my_recipes
import saltpepper.core.ui.generated.resources.profile_no_favorites
import saltpepper.core.ui.generated.resources.profile_no_recipes
import saltpepper.core.ui.generated.resources.profile_recipe_minutes
import saltpepper.core.ui.generated.resources.profile_write_recipe
import saltpepper.core.ui.generated.resources.settings_title
import saltpepper.core.ui.generated.resources.setup_entry

@Composable
fun ProfileScreen(
	modifier: Modifier = Modifier,
    onSettings: () -> Unit,
    onProfileSetup: () -> Unit,
    onBrowseRecipes: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }

    ProfileScreenContent(
        modifier = modifier,
        state = state,
        onSettings = onSettings,
        onProfileSetup = onProfileSetup,
        onBrowseRecipes = onBrowseRecipes,
        onRetry = viewModel::refresh
    )
}

@Composable
private fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    state: ProfileUiState = ProfileUiState(),
    onSettings: () -> Unit = {},
    onProfileSetup: () -> Unit = {},
    onBrowseRecipes: () -> Unit = {},
    onRetry: () -> Unit = {}
) {
    when (val screenState = state.screenState) {
        ProfileScreenUiState.Loading -> LoadingScreen(modifier = modifier)
        is ProfileScreenUiState.Error -> ErrorScreen(
            modifier = modifier,
            message = screenState.message,
            onRetry = onRetry
        )
        is ProfileScreenUiState.Success -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "header") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            screenState.profile.displayName ?: screenState.profile.email,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.headlineLarge
                        )
                        IconButton(onClick = onSettings) {
                            Icon(
                                Icons.Outlined.Settings,
                                contentDescription = stringResource(Res.string.settings_title)
                            )
                        }
                    }
                    Text(
                        text = stringResource(
                            Res.string.profile_member_since,
                            formatDate(screenState.profile.memberSince)
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            recipeSection(
                key = "favorites",
                title = { stringResource(Res.string.profile_favorites) },
                recipes = screenState.favorites,
                emptyContent = {
                    Text(stringResource(Res.string.profile_no_favorites))
                    PrimaryButton(
                        text = stringResource(Res.string.browse_recipes_action),
                        onClick = onBrowseRecipes
                    )
                }
            )
            recipeSection(
                key = "my-recipes",
                title = { stringResource(Res.string.profile_my_recipes) },
                recipes = screenState.myRecipes,
                emptyContent = {
                    Text(stringResource(Res.string.profile_no_recipes))
                    Text(
                        stringResource(Res.string.profile_creation_coming_soon),
                        style = MaterialTheme.typography.bodySmall
                    )
                    PrimaryButton(
                        text = stringResource(Res.string.profile_import_recipe),
                        onClick = {},
                        enabled = false
                    )
                    PrimaryButton(
                        text = stringResource(Res.string.profile_write_recipe),
                        onClick = {},
                        enabled = false
                    )
                }
            )
            item(key = "profile-setup") {
                    PrimaryButton(
                        onClick = onProfileSetup,
                        text = stringResource(Res.string.setup_entry),
                        enabled = !state.isLoading
                    )
            }
        }
    }

}

private fun LazyListScope.recipeSection(
    key: String,
    title: @Composable () -> String,
    recipes: List<ProfileRecipe>,
    emptyContent: @Composable () -> Unit
) {
    item(key = "$key-title") {
        Text(title(), style = MaterialTheme.typography.titleLarge)
    }
    if (recipes.isEmpty()) {
        item(key = "$key-empty") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { emptyContent() }
        }
    } else {
        items(recipes, key = { "$key-${it.id}" }) { recipe ->
            Card(modifier = Modifier.fillMaxWidth()) {
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
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
	SaltPepperTheme {
		ProfileScreenContent(
            state = ProfileUiState(
                screenState = ProfileScreenUiState.Success(
                    profile = MemberProfile("Camille", "camille@example.test", "2026-11-10"),
                    favorites = emptyList(),
                    myRecipes = emptyList()
                )
            )
		)
	}
}

@Preview
@Composable
private fun ProfileRecipesPreview() {
    SaltPepperTheme {
        ProfileScreenContent(
            state = ProfileUiState(
                screenState = ProfileScreenUiState.Success(
                    profile = MemberProfile("Camille", "camille@example.test", "2026-11-10"),
                    favorites = listOf(ProfileRecipe("favorite-1", "Vegetable curry", 35)),
                    myRecipes = listOf(ProfileRecipe("recipe-1", "Tomato soup", 20))
                )
            )
        )
    }

}