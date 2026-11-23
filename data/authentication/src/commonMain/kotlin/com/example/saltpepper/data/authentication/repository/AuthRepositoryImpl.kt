package com.example.saltpepper.data.authentication.repository

import com.example.saltpepper.core.network.model.UserApiModel
import com.example.saltpepper.core.session.SessionManager
import com.example.saltpepper.data.authentication.datasource.AuthenticationRemoteDataSource
import com.example.saltpepper.data.authentication.model.User
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class AuthRepositoryImpl(
    private val remoteDataSource: AuthenticationRemoteDataSource,
    private val session: SessionManager,
    scope: CoroutineScope
) : AuthRepository {
    private val operationState = MutableStateFlow<AuthState?>(null)
    override val authState = combine(session.user, operationState) { user, operation ->
        operation ?: user?.let { AuthState.Authenticated(User(it.id, it.email)) }
            ?: AuthState.Unauthenticated
    }.stateIn(scope, SharingStarted.Eagerly, AuthState.Unauthenticated)

    override suspend fun requestEmailCode(email: String): Result<Unit> =
        authenticate { remoteDataSource.requestEmailCode(email) }

    override suspend fun register(email: String, password: String): Result<Unit> =
        authenticate { remoteDataSource.register(email, password) }

    override suspend fun verifyEmailCode(email: String, code: String): Result<User> =
        authenticate { remoteDataSource.verifyEmailCode(email, code).asModel() }

    override suspend fun verifyRegistrationCode(email: String, code: String): Result<User> =
        authenticate { remoteDataSource.verifyRegistrationCode(email, code).asModel() }

    override suspend fun logout(): Result<Unit> = try {
        request { remoteDataSource.logout() }
    } finally {
        operationState.value = null
    }

    override suspend fun getCurrentUser(): Result<User?> =
        Result.success(session.user.value?.let { User(it.id, it.email) })

    override suspend fun restoreSession(): Result<User?> = request {
        remoteDataSource.restoreSession()?.asModel().also { operationState.value = null }
    }

    private fun UserApiModel.asModel() = User(id, email)

    private suspend fun <T> authenticate(block: suspend () -> T): Result<T> {
        operationState.value = AuthState.Loading
        return try {
            Result.success(block()).also { operationState.value = null }
        } catch (error: CancellationException) {
            operationState.value = null
            throw error
        } catch (error: Exception) {
            operationState.value = AuthState.Error(error.message ?: "Authentication failed")
            Result.failure(error)
        }
    }

    private suspend fun <T> request(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Result.failure(error)
    }
}
