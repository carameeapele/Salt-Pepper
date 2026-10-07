package com.example.cuisinonsensemble.data.repository

import com.example.cuisinonsensemble.data.model.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>

    suspend fun requestEmailCode(email: String): Result<Unit>
    suspend fun register(email: String, password: String): Result<Unit>
    suspend fun verifyEmailCode(email: String, code: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
}

sealed class AuthState {
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
    object Loading : AuthState()
}