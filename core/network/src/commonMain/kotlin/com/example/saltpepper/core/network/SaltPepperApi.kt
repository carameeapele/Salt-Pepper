package com.example.saltpepper.core.network

import com.example.saltpepper.core.network.model.ProfileApiModel
import com.example.saltpepper.core.network.model.RecipeApiModel
import com.example.saltpepper.core.network.model.UserApiModel

interface SaltPepperApi {
    suspend fun requestEmailCode(email: String)
    suspend fun register(email: String, password: String)
    suspend fun verifyEmailCode(email: String, code: String): UserApiModel
    suspend fun verifyRegistrationCode(email: String, code: String): UserApiModel
    suspend fun restoreSession(): UserApiModel?
    suspend fun logout()
    suspend fun getProfile(): ProfileApiModel
    suspend fun updateDisplayName(displayName: String)
    suspend fun requestAccountDeletionCode()
    suspend fun deleteAccount(code: String, deleteCreatedRecipes: Boolean)
    suspend fun getMyRecipes(): List<RecipeApiModel>
    suspend fun getFavoriteRecipes(): List<RecipeApiModel>
}
