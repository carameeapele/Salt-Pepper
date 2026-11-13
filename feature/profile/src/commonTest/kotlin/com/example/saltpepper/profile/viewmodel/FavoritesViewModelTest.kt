package com.example.saltpepper.profile.viewmodel

import com.example.saltpepper.data.recipe.model.Recipe
import com.example.saltpepper.data.recipe.repository.RecipeRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {
    @Test
    fun errorCanBeRetriedToEmptyAndPopulatedSuccess() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repository = FakeRepository()
            val viewModel = FavoritesViewModel(repository)
            assertIs<FavoritesUiState.Loading>(viewModel.uiState.value)
            repository.result = Result.failure(Exception("Favorites unavailable"))
            viewModel.refresh()
            runCurrent()
            assertEquals(FavoritesUiState.Error("Favorites unavailable"), viewModel.uiState.value)
            repository.result = Result.success(emptyList())
            viewModel.refresh()
            assertIs<FavoritesUiState.Loading>(viewModel.uiState.value)
            runCurrent()
            assertEquals(FavoritesUiState.Success(emptyList()), viewModel.uiState.value)
            val recipes = listOf(Recipe("1", "Soup", 20), Recipe("2", "Curry", 35))
            repository.result = Result.success(recipes)
            viewModel.refresh()
            runCurrent()
            assertEquals(FavoritesUiState.Success(recipes), viewModel.uiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun duplicateRefreshesShareOneInFlightRequest() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val gate = CompletableDeferred<Unit>()
            val repository = FakeRepository().apply { this.gate = gate }
            val viewModel = FavoritesViewModel(repository)
            viewModel.refresh()
            viewModel.refresh()
            runCurrent()
            assertEquals(1, repository.calls)
            assertIs<FavoritesUiState.Loading>(viewModel.uiState.value)
            gate.complete(Unit)
            runCurrent()
            assertIs<FavoritesUiState.Success>(viewModel.uiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private class FakeRepository : RecipeRepository {
        var result: Result<List<Recipe>> = Result.success(emptyList())
        var gate: CompletableDeferred<Unit>? = null
        var calls = 0

        override suspend fun getFavorites(): Result<List<Recipe>> {
            calls++
            gate?.await()
            return result
        }

        override suspend fun getMyRecipes(): Result<List<Recipe>> = error("Unexpected recipes request")
    }
}
