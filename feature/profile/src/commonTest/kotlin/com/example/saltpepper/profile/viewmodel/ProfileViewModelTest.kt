package com.example.saltpepper.profile.viewmodel

import com.example.saltpepper.data.model.MemberProfile
import com.example.saltpepper.data.model.ProfileRecipe
import com.example.saltpepper.data.model.User
import com.example.saltpepper.data.repository.AuthRepository
import com.example.saltpepper.data.repository.AuthState
import com.example.saltpepper.data.repository.ProfileRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    @Test
    fun deletionRequiresRequestedCodeAndBlocksDuplicateSubmission() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repository = FakeRepository()
            val viewModel = ProfileViewModel(repository, FakeProfileRepository())
            viewModel.openDeletionDialog()
            viewModel.onCodeChange("123456")
            viewModel.deleteAccount()
            assertEquals(0, repository.deleteCalls)
            viewModel.requestDeletionCode()
            viewModel.requestDeletionCode()
            assertTrue(viewModel.uiState.value.isLoading)
            runCurrent()
            assertEquals(1, repository.codeCalls)
            assertTrue(viewModel.uiState.value.codeSent)
            assertFalse(viewModel.uiState.value.deleteCreatedRecipes)
            viewModel.onCodeChange("12a34567")
            assertEquals("123456", viewModel.uiState.value.code)
            viewModel.onDeleteCreatedRecipesChange(true)
            viewModel.deleteAccount()
            viewModel.deleteAccount()
            runCurrent()
            assertEquals(1, repository.deleteCalls)
            assertEquals("123456" to true, repository.deletion)
            assertTrue(viewModel.uiState.value.accountDeleted)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun failuresStayInDialogAndAllowRetryOrCancellation() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repository = FakeRepository()
            val viewModel = ProfileViewModel(repository, FakeProfileRepository())
            viewModel.openDeletionDialog()
            repository.failure = Exception("Try later")
            viewModel.requestDeletionCode()
            runCurrent()
            assertEquals("Try later", viewModel.uiState.value.errorMessage)
            assertFalse(viewModel.uiState.value.codeSent)
            repository.failure = null
            viewModel.requestDeletionCode()
            runCurrent()
            viewModel.onCodeChange("123456")
            repository.failure = Exception("Invalid or expired code")
            viewModel.deleteAccount()
            runCurrent()
            assertFalse(viewModel.uiState.value.accountDeleted)
            assertTrue(viewModel.uiState.value.showDeletionDialog)
            assertEquals("Invalid or expired code", viewModel.uiState.value.errorMessage)
            viewModel.dismissDeletionDialog()
            assertEquals(ProfileUiState(), viewModel.uiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private class FakeProfileRepository : ProfileRepository {
        var failFavorites = false
        var gate: CompletableDeferred<Unit>? = null
        var profileCalls = 0
        override suspend fun getProfile() =
            Result.success(MemberProfile("Camille", "user@example.test", "2026-11-10")).also {
                profileCalls++
                gate?.await()
            }
        override suspend fun getFavorites(): Result<List<ProfileRecipe>> =
            if (failFavorites) Result.failure(Exception("Favorites unavailable"))
            else Result.success(emptyList())
        override suspend fun getMyRecipes() =
            Result.success(listOf(ProfileRecipe("recipe-1", "Soup", 20)))
    }

    @Test
    fun profileErrorCanBeRetriedAndSuccessSurvivesDeletionDialogDismissal() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val profileRepository = FakeProfileRepository().apply { failFavorites = true }
            val viewModel = ProfileViewModel(FakeRepository(), profileRepository)
            assertIs<ProfileScreenUiState.Loading>(viewModel.uiState.value.screenState)
            viewModel.refresh()
            runCurrent()
            val error = assertIs<ProfileScreenUiState.Error>(viewModel.uiState.value.screenState)
            assertEquals("Favorites unavailable", error.message)
            profileRepository.failFavorites = false
            viewModel.refresh()
            assertIs<ProfileScreenUiState.Loading>(viewModel.uiState.value.screenState)
            runCurrent()
            val success = assertIs<ProfileScreenUiState.Success>(viewModel.uiState.value.screenState)
            assertEquals("Camille", success.profile.displayName)
            assertEquals("2026-11-10", success.profile.memberSince)
            assertEquals("Soup", success.myRecipes.single().title)
            assertEquals(emptyList(), success.favorites)
            viewModel.openDeletionDialog()
            viewModel.dismissDeletionDialog()
            assertEquals(success, viewModel.uiState.value.screenState)
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
            val viewModel = ProfileViewModel(FakeRepository(), repository)
            viewModel.refresh()
            viewModel.refresh()
            runCurrent()
            assertIs<ProfileScreenUiState.Loading>(viewModel.uiState.value.screenState)
            assertEquals(1, repository.profileCalls)
            gate.complete(Unit)
            runCurrent()
            assertIs<ProfileScreenUiState.Success>(viewModel.uiState.value.screenState)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private class FakeRepository : AuthRepository {
        override val authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
        var failure: Exception? = null
        var codeCalls = 0
        var deleteCalls = 0
        var deletion: Pair<String, Boolean>? = null

        override suspend fun requestAccountDeletionCode(): Result<Unit> {
            codeCalls++
            return failure?.let { Result.failure(it) } ?: Result.success(Unit)
        }

        override suspend fun deleteAccount(code: String, deleteCreatedRecipes: Boolean): Result<Unit> {
            deleteCalls++
            deletion = code to deleteCreatedRecipes
            return failure?.let { Result.failure(it) } ?: Result.success(Unit)
        }

        override suspend fun requestEmailCode(email: String): Result<Unit> = error("Unexpected call")
        override suspend fun register(email: String, password: String): Result<Unit> = error("Unexpected call")
        override suspend fun verifyEmailCode(email: String, code: String): Result<User> = error("Unexpected call")
        override suspend fun verifyRegistrationCode(email: String, code: String): Result<User> = error("Unexpected call")
        override suspend fun updateDisplayName(displayName: String): Result<Unit> = error("Unexpected call")
        override suspend fun logout(): Result<Unit> = error("Unexpected call")
        override suspend fun getCurrentUser(): Result<User?> = error("Unexpected call")
        override suspend fun restoreSession(): Result<User?> = error("Unexpected call")
    }
}
