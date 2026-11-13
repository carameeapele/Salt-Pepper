package com.example.saltpepper.data.profile.datasource

import com.example.saltpepper.core.network.SaltPepperApi

class ProfileRemoteDataSource(private val api: SaltPepperApi) {
    suspend fun getProfile() = api.getProfile()
    suspend fun updateDisplayName(displayName: String) = api.updateDisplayName(displayName)
    suspend fun requestAccountDeletionCode() = api.requestAccountDeletionCode()
    suspend fun deleteAccount(code: String, deleteCreatedRecipes: Boolean) =
        api.deleteAccount(code, deleteCreatedRecipes)
}
