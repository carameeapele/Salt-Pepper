package com.example.saltpepper.profile.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
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
import saltpepper.core.ui.generated.resources.profile_favorites_card_title
import saltpepper.core.ui.generated.resources.profile_import_recipe
import saltpepper.core.ui.generated.resources.profile_member_since
import saltpepper.core.ui.generated.resources.profile_my_recipes_card_title
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

@Composable
private fun ProfileScreenContent(
    modifier: Modifier = Modifier,
    state: ProfileScreenUiState = ProfileScreenUiState.Loading,
    onNavigateToSettings: () -> Unit = {},
    onHouseholdEdit: () -> Unit = {},
    onNavigateToFavoriteRecipes: () -> Unit = {},
    onRetry: () -> Unit = {}
) {
    when (state) {
        ProfileScreenUiState.Loading -> LoadingScreen()
        is ProfileScreenUiState.Error -> ErrorScreen(
            message = state.message,
            onRetry = onRetry
        )
        is ProfileScreenUiState.Success -> Column(
            modifier = modifier.fillMaxSize()
        ) {
            ProfileHeader(
                screenState = state,
                onNavigateToSettings = onNavigateToSettings
            )
            Spacer(modifier.height(16.dp))
            CustomOutlinedButton(
                modifier = modifier.fillMaxWidth(),
                buttonVariant = ButtonVariant.SMALL,
                onClick = onHouseholdEdit,
                text = stringResource(Res.string.setup_settings),
                leftIcon = Icons.Outlined.Home
            )
            Spacer(modifier.height(20.dp))
            Text(
                text = stringResource(Res.string.profile_my_recipes_card_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier.height(16.dp))
            FavoritesCard(
                onNavigateToFavoriteRecipes = onNavigateToFavoriteRecipes
            )
            Spacer(modifier.height(20.dp))

            if (state.myRecipes.isEmpty()) {
                PrimaryButton(
                    modifier = modifier.fillMaxWidth(),
                    text = stringResource(Res.string.profile_import_recipe),
                    onClick = {},
                    enabled = false
                )
                Spacer(modifier.height(16.dp))
                PrimaryButton(
                    modifier = modifier.fillMaxWidth(),
                    text = stringResource(Res.string.profile_write_recipe),
                    onClick = {},
                    enabled = false
                )
            } else {
                LazyColumn {
                    items(
                        items = state.myRecipes,
                        key = { "recipe-${it.id}" }
                    ) { recipe ->
                        ProfileRecipeCard(recipe)
                    }
                }
            }
        }
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
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Face,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = modifier.width(10.dp))

            Text(
                text = screenState.profile.displayName,
                modifier = modifier.weight(1f),
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FavoritesCard(
    modifier: Modifier = Modifier,
    onNavigateToFavoriteRecipes: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        onClick = onNavigateToFavoriteRecipes
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = ""
            )
            Spacer(modifier.width(12.dp))

            Column(
                modifier = modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(Res.string.profile_favorites_card_title),

                    style = MaterialTheme.typography.titleMediumEmphasized
                )
                Text(
                    stringResource(Res.string.favorites_card_description),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(Res.string.profile_favorites_card_title)
            )
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