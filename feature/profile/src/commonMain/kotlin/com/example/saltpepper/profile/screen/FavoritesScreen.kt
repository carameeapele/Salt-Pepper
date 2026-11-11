package com.example.saltpepper.profile.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.saltpepper.core.ui.component.button.ButtonVariant
import com.example.saltpepper.core.ui.component.button.CustomTextButton
import com.example.saltpepper.core.ui.component.button.PrimaryButton
import com.example.saltpepper.core.ui.component.screen.ErrorScreen
import com.example.saltpepper.core.ui.component.screen.LoadingScreen
import com.example.saltpepper.core.ui.theme.SaltPepperTheme
import com.example.saltpepper.data.model.ProfileRecipe
import com.example.saltpepper.profile.components.ProfileRecipeCard
import com.example.saltpepper.profile.viewmodel.FavoritesUiState
import com.example.saltpepper.profile.viewmodel.FavoritesViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import saltpepper.core.ui.generated.resources.Res
import saltpepper.core.ui.generated.resources.back
import saltpepper.core.ui.generated.resources.browse_recipes_action
import saltpepper.core.ui.generated.resources.favorites_empty_description
import saltpepper.core.ui.generated.resources.profile_favorites_card_title
import saltpepper.core.ui.generated.resources.profile_no_favorites

@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    onBrowseRecipes: () -> Unit,
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.refresh() }
    FavoritesScreenContent(state, onBack, onBrowseRecipes, viewModel::refresh)
}

@Composable
private fun FavoritesScreenContent(
    state: FavoritesUiState,
    onBack: () -> Unit = {},
    onBrowseRecipes: () -> Unit = {},
    onRetry: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CustomTextButton(
                buttonVariant = ButtonVariant.MEDIUM,
                text = stringResource(Res.string.back),
                leftIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onClick = onBack
            )
            Text(
                stringResource(Res.string.profile_favorites_card_title),
                style = MaterialTheme.typography.headlineLarge
            )
        }
        when (state) {
            FavoritesUiState.Loading -> LoadingScreen(modifier = Modifier.weight(1f))
            is FavoritesUiState.Error -> ErrorScreen(
                modifier = Modifier.weight(1f),
                message = state.message,
                onRetry = onRetry
            )
            is FavoritesUiState.Success -> LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (state.recipes.isEmpty()) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(Res.string.profile_no_favorites),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                stringResource(Res.string.favorites_empty_description),
                                textAlign = TextAlign.Center
                            )
                            PrimaryButton(
                                text = stringResource(Res.string.browse_recipes_action),
                                onClick = onBrowseRecipes
                            )
                        }
                    }
                } else {
                    items(state.recipes, key = { it.id }) { ProfileRecipeCard(it) }
                }
            }
        }
    }
}

@Preview
@Composable
private fun EmptyFavoritesPreview() {
    SaltPepperTheme { FavoritesScreenContent(FavoritesUiState.Success(emptyList())) }
}

@Preview
@Composable
private fun FavoritesPreview() {
    SaltPepperTheme {
        FavoritesScreenContent(
            FavoritesUiState.Success(listOf(ProfileRecipe("1", "Vegetable curry", 35)))
        )
    }
}
