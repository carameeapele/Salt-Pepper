package com.example.saltpepper.splash.viewmodel

import com.example.saltpepper.data.model.User
import com.example.saltpepper.data.repository.AuthRepository
import com.example.saltpepper.data.repository.AuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SplashScreenViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val user = User(id = "user-1", email = "user@example.com")

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun authenticatedSessionProducesSuccess() = runTest(dispatcher) {
        val repository = FakeAuthRepository(user = user)
        val viewModel = SplashScreenViewModel(repository)

        assertEquals(SplashScreenUiState.Loading, viewModel.uiState.value)
        assertEquals(0, repository.restoreSessionCalls)

        advanceUntilIdle()

        assertEquals(SplashScreenUiState.Success(true), viewModel.uiState.value)
        assertEquals(1, repository.restoreSessionCalls)
        assertEquals(1500L, testScheduler.currentTime)
    }

    @Test
    fun absentSessionProducesUnauthenticatedSuccess() = runTest(dispatcher) {
        val repository = FakeAuthRepository()
        val viewModel = SplashScreenViewModel(repository)

        assertEquals(SplashScreenUiState.Loading, viewModel.uiState.value)
        assertEquals(0, repository.restoreSessionCalls)

        advanceUntilIdle()

        assertEquals(SplashScreenUiState.Success(false), viewModel.uiState.value)
        assertEquals(1, repository.restoreSessionCalls)
        assertEquals(1500L, testScheduler.currentTime)
    }

    @Test
    fun networkFailureCanRetrySuccessfullyWithoutRepeatingStartupDelay() = runTest(dispatcher) {
        val repository = FakeAuthRepository(
            user = user,
            failure = IllegalStateException("Network unavailable")
        )
        val viewModel = SplashScreenViewModel(repository)

        assertEquals(SplashScreenUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()

        assertEquals(SplashScreenUiState.Error, viewModel.uiState.value)
        assertEquals(1, repository.restoreSessionCalls)
        assertEquals(1500L, testScheduler.currentTime)
        val retryStartedAt = testScheduler.currentTime
        repository.failure = null

        viewModel.retry()

        assertEquals(SplashScreenUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()

        assertEquals(SplashScreenUiState.Success(true), viewModel.uiState.value)
        assertEquals(2, repository.restoreSessionCalls)
        assertEquals(retryStartedAt, testScheduler.currentTime)
    }

    private class FakeAuthRepository(
        private val user: User? = null,
        var failure: Throwable? = null
    ) : AuthRepository {
        override val authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
        var restoreSessionCalls = 0
            private set

        override suspend fun requestEmailCode(email: String): Result<Unit> =
            error("Unexpected requestEmailCode call")

        override suspend fun register(email: String, password: String): Result<Unit> =
            error("Unexpected register call")

        override suspend fun verifyEmailCode(email: String, code: String): Result<User> =
            error("Unexpected verifyEmailCode call")

        override suspend fun logout(): Result<Unit> =
            error("Unexpected logout call")

        override suspend fun getCurrentUser(): Result<User?> =
            error("Unexpected getCurrentUser call")

        override suspend fun restoreSession(): Result<User?> {
            restoreSessionCalls += 1
            val injectedFailure = failure
            if (injectedFailure != null) {
                authState.value = AuthState.Error(injectedFailure.message.orEmpty())
                return Result.failure(injectedFailure)
            }
            authState.value = user?.let { AuthState.Authenticated(it) } ?: AuthState.Unauthenticated
            return Result.success(user)
        }
    }
}