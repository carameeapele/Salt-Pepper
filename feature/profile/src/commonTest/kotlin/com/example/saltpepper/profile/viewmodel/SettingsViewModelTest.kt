package com.example.saltpepper.profile.viewmodel

import com.example.saltpepper.data.profile.model.MemberProfile
import com.example.saltpepper.data.profile.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @Test
    fun deletionRequiresRequestedCodeAndBlocksDuplicateSubmission() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val repository = FakeRepository()
            val viewModel = SettingsViewModel(repository)
            viewModel.openDeletionDialog()
            viewModel.onCodeChange("123456")
            viewModel.deleteAccount()
            assertEquals(0, repository.deleteCalls)
            viewModel.requestDeletionCode()
            viewModel.requestDeletionCode()
            assertTrue(viewModel.isLoading.value)
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
            val viewModel = SettingsViewModel(repository)
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
            assertEquals(SettingsUiState(), viewModel.uiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    private class FakeRepository : ProfileRepository {
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

        override suspend fun updateDisplayName(displayName: String): Result<Unit> = error("Unexpected call")
        override suspend fun getProfile(): Result<MemberProfile> = error("Unexpected call")
    }
}
