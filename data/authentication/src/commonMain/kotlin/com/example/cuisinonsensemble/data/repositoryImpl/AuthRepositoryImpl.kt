package com.example.cuisinonsensemble.data.repositoryImpl

import com.example.cuisinonsensemble.data.model.User
import com.example.cuisinonsensemble.data.repository.AuthRepository
import com.example.cuisinonsensemble.data.repository.AuthState
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            _authState.value = AuthState.Loading
            supabaseClient.auth.signInWith {
                this.email = email
                this.password = password
            }
            _authState.value = AuthState.Authenticated(
                User(
                    id = supabaseClient.auth.currentUserOrNull()?.id ?: "",
                    email = email
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Login failed")
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            _authState.value = AuthState.Loading
            supabaseClient.auth.signUpWith {
                this.email = email
                this.password = password
            }
            _authState.value = AuthState.Authenticated(User(
                id = supabaseClient.auth.currentUserOrNull()?.id ?: "",
                email = email
            ))
            Result.success(Unit)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Registration failed")
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            supabaseClient.auth.signOut()
            _authState.value = AuthState.Unauthenticated
            Result.success(Unit)
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message ?: "Logout failed")
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val user = supabaseClient.auth.currentUserOrNull()
            Result.success(user?.let { User(it.id, it.email ?: "") })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}