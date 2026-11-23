package com.example.saltpepper.profile.viewmodel

import com.example.saltpepper.data.profile.model.MemberProfile
import com.example.saltpepper.data.profile.repository.ProfileRepository
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
class ProfileViewModelTest {
    private class FakeProfileRepository : ProfileRepository, RecipeRepository {
        var failProfile = false
        var gate: CompletableDeferred<Unit>? = null
        var profileCalls = 0
        override suspend fun getProfile() =
            (if (failProfile) Result.failure(Exception("Profile unavailable"))
            else Result.success(MemberProfile("Camille", "user@example.test", "2026-11-10"))).also {
                profileCalls++
                gate?.await()
            }
        override suspend fun getFavorites(): Result<List<Recipe>> =
            error("Profile must not load favorites")
        override suspend fun getMyRecipes() =
            Result.success(listOf(Recipe("recipe-1", "Soup", 20)))
        override suspend fun updateDisplayName(displayName: String): Result<Unit> = error("Unexpected call")
        override suspend fun requestAccountDeletionCode(): Result<Unit> = error("Unexpected call")
        override suspend fun deleteAccount(code: String, deleteCreatedRecipes: Boolean): Result<Unit> =
            error("Unexpected call")
    }

    @Test
    fun profileErrorCanBeRetried() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val profileRepository = FakeProfileRepository().apply { failProfile = true }
            val viewModel = ProfileViewModel(profileRepository, profileRepository)
            assertIs<ProfileScreenUiState.Loading>(viewModel.uiState.value)
            viewModel.refresh()
            runCurrent()
            val error = assertIs<ProfileScreenUiState.Error>(viewModel.uiState.value)
            assertEquals("Profile unavailable", error.message)
            profileRepository.failProfile = false
            viewModel.refresh()
            assertIs<ProfileScreenUiState.Loading>(viewModel.uiState.value)
            runCurrent()
            val success = assertIs<ProfileScreenUiState.Success>(viewModel.uiState.value)
            assertEquals("Camille", success.profile.displayName)
            assertEquals("2026-11-10", success.profile.memberSince)
            assertEquals("Soup", success.myRecipes.single().title)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun loadingWaitsForAllRequestsAndPreventsDuplicateRefreshes() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val gate = CompletableDeferred<Unit>()
            val repository = FakeProfileRepository().apply { this.gate = gate }
            val viewModel = ProfileViewModel(repository, repository)
            viewModel.refresh()
            viewModel.refresh()
            runCurrent()
            assertIs<ProfileScreenUiState.Loading>(viewModel.uiState.value)
            assertEquals(1, repository.profileCalls)
            gate.complete(Unit)
            runCurrent()
            assertIs<ProfileScreenUiState.Success>(viewModel.uiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
