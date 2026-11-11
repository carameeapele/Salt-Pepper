package com.example.saltpepper.data.repository

import com.example.saltpepper.data.client.BackendAuthClient
import com.example.saltpepper.data.model.MemberProfile
import com.example.saltpepper.data.model.ProfileRecipe
import kotlinx.coroutines.CancellationException

interface ProfileRepository {
    suspend fun getProfile(): Result<MemberProfile>
    suspend fun getFavorites(): Result<List<ProfileRecipe>>
    suspend fun getMyRecipes(): Result<List<ProfileRecipe>>
}

class BackendProfileRepository(private val client: BackendAuthClient) : ProfileRepository {
    override suspend fun getProfile() = request { client.getProfile() }
    override suspend fun getFavorites() = request { client.getProfileRecipes(favorites = true) }
    override suspend fun getMyRecipes() = request { client.getProfileRecipes(favorites = false) }

    private suspend fun <T> request(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Result.failure(error)
    }
}
