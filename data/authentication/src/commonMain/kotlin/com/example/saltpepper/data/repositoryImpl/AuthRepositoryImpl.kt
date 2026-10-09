package com.example.saltpepper.data.repositoryImpl

import com.example.saltpepper.data.client.BackendAuthClient
import com.example.saltpepper.data.model.User
import com.example.saltpepper.data.repository.AuthRepository
import com.example.saltpepper.data.repository.AuthState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl(
    private val backendClient: BackendAuthClient
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun requestEmailCode(email: String): Result<Unit> {
        return try {
            _authState.value = AuthState.Loading
            backendClient.requestEmailCode(email)
            _authState.value = AuthState.Unauthenticated
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Could not send verification code")
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun verifyEmailCode(email: String, code: String): Result<User> {
        return try {
            _authState.value = AuthState.Loading
            val user = backendClient.verifyEmailCode(email, code)
            _authState.value = AuthState.Authenticated(user)
            Result.success(user)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Verification failed")
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            backendClient.logout()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _authState.value = AuthState.Unauthenticated
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return Result.success(backendClient.currentUser)
    }

    override suspend fun restoreSession(): Result<User?> {
        return try {
            val user = backendClient.restoreSession()
            _authState.value = user?.let { AuthState.Authenticated(it) } ?: AuthState.Unauthenticated
            Result.success(user)
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            Result.failure(error)
        }
    }
}