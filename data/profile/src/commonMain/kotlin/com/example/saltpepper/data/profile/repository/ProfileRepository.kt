package com.example.saltpepper.data.profile.repository

import com.example.saltpepper.data.profile.model.MemberProfile

interface ProfileRepository {
    suspend fun getProfile(): Result<MemberProfile>
    suspend fun updateDisplayName(displayName: String): Result<Unit>
    suspend fun requestAccountDeletionCode(): Result<Unit>
    suspend fun deleteAccount(code: String, deleteCreatedRecipes: Boolean): Result<Unit>
}
