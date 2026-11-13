package com.example.saltpepper.profile.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import com.example.saltpepper.core.ui.component.button.ButtonVariant
import com.example.saltpepper.core.ui.component.button.CustomOutlinedButton
import com.example.saltpepper.core.ui.component.button.PrimaryButton
import com.example.saltpepper.core.ui.component.screen.ErrorScreen
import com.example.saltpepper.core.ui.component.screen.LoadingScreen
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import com.example.saltpepper.core.ui.util.formatDate
import com.example.saltpepper.data.recipe.model.Recipe
import com.example.saltpepper.profile.components.ProfileRecipeCard
import com.example.saltpepper.profile.model.ProfileUiModel
import com.example.saltpepper.profile.viewmodel.ProfileScreenUiState
import com.example.saltpepper.profile.viewmodel.ProfileViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.favorites_card_description
import saltpepper.core.ui.generated.resources.profile_creation_coming_soon
import saltpepper.core.ui.generated.resources.profile_favorites_card_title
import saltpepper.core.ui.generated.resources.profile_import_recipe
import saltpepper.core.ui.generated.resources.profile_member_since
import saltpepper.core.ui.generated.resources.profile_my_recipes_card_title
import saltpepper.core.ui.generated.resources.profile_no_recipes
import saltpepper.core.ui.generated.resources.profile_write_recipe
import saltpepper.core.ui.generated.resources.settings_title
import saltpepper.core.ui.generated.resources.setup_settings

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onHouseholdEdit: () -> Unit,
    onNavigateToFavoriteRecipes: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refresh()
    }

    ProfileScreenContent(
        state = state,
        onNavigateToSettings = onNavigateToSettings,
        onHouseholdEdit = onHouseholdEdit,
        onNavigateToFavoriteRecipes = onNavigateToFavoriteRecipes,
        onRetry = viewModel::refresh
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    state: ProfileScreenUiState = ProfileScreenUiState.Loading,
    onNavigateToSettings: () -> Unit = {},
    onHouseholdEdit: () -> Unit = {},
    onNavigateToFavoriteRecipes: () -> Unit = {},
    onRetry: () -> Unit = {}
) {
    when (val screenState = state) {
        ProfileScreenUiState.Loading -> LoadingScreen()
        is ProfileScreenUiState.Error -> ErrorScreen(
            message = screenState.message,
            onRetry = onRetry
        )
        is ProfileScreenUiState.Success -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(key = "header") {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            screenState.profile.displayName,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.headlineLarge
                        )
                        IconButton(
                            onClick = onNavigateToSettings
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
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
            item(key = "household-setup") {
                CustomOutlinedButton(
                    buttonVariant = ButtonVariant.SMALL,
                    onClick = onHouseholdEdit,
                    text = stringResource(Res.string.setup_settings),
                    leftIcon = Icons.Outlined.Home
                )
            }
            item(key = "favorites") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    onClick = onNavigateToFavoriteRecipes
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical =12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                stringResource(Res.string.profile_favorites_card_title),
                                style = MaterialTheme.typography.titleLargeEmphasized
                            )
                            Text(
                                stringResource(Res.string.favorites_card_description),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = modifier.width(16.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = stringResource(Res.string.profile_favorites_card_title)
                        )
                    }
                }
            }
            recipeSection(
                key = "my-recipes",
                title = { stringResource(Res.string.profile_my_recipes_card_title) },
                recipes = screenState.myRecipes,
                emptyContent = {
                    Text(
                        text = stringResource(Res.string.profile_no_recipes)
                    )
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
        }
    }

}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun LazyListScope.recipeSection(
    key: String,
    title: @Composable () -> String,
    recipes: List<Recipe>,
    emptyContent: @Composable () -> Unit
) {
    item(key = "$key-title") {
        Text(
            text = title(),
            style = MaterialTheme.typography.titleLargeEmphasized
        )
    }
    if (recipes.isEmpty()) {
        item(key = "$key-empty") {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                emptyContent()
            }
        }
    } else {
        items(recipes, key = { "$key-${it.id}" }) { recipe ->
            ProfileRecipeCard(recipe)
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
	SaltPepperTheme {
		ProfileScreenContent(
            state = ProfileScreenUiState.Success(
                    profile = ProfileUiModel(
                        displayName = "Camille",
                        email = "camille@example.test",
                        memberSince = "2026-11-10"
                    ),
                    myRecipes = emptyList()
                )
		)
	}
}

@Composable
private fun ProfileHeader(
    modifier: Modifier = Modifier,
    screenState: ProfileScreenUiState.Success,
    onNavigateToSettings: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                screenState.profile.displayName,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineLarge
            )
            IconButton(
                onClick = onNavigateToSettings
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
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

@Preview
@Composable
private fun ProfileRecipesPreview() {
    SaltPepperTheme {
        ProfileScreenContent(
            state = ProfileScreenUiState.Success(
                    profile = ProfileUiModel(
                        displayName = "Camille",
                        email = "camille@example.test",
                        memberSince = "2026-11-10"
                    ),
                    myRecipes = listOf(
                        Recipe(
                            id = "recipe-1",
                            title = "Tomato soup",
                            totalTimeMinutes = 20
                        )
                    )
                )
        )
    }

}