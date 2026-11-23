package com.example.saltpepper.data.profile.repository

import com.example.saltpepper.data.profile.datasource.ProfileRemoteDataSource
import com.example.saltpepper.data.profile.model.MemberProfile
import kotlinx.coroutines.CancellationException
import kotlin.time.Instant

class ProfileRepositoryImpl(
    private val remoteDataSource: ProfileRemoteDataSource
) : ProfileRepository {
    override suspend fun getProfile(): Result<MemberProfile> = request {
        val profile = remoteDataSource.getProfile()
        MemberProfile(
            displayName = profile.displayName,
            email = profile.email,
            memberSince = Instant.parse(profile.createdAt).toString().substringBefore('T')
        )
    }

    override suspend fun updateDisplayName(displayName: String): Result<Unit> =
        request { remoteDataSource.updateDisplayName(displayName) }

    override suspend fun requestAccountDeletionCode(): Result<Unit> =
        request { remoteDataSource.requestAccountDeletionCode() }

    override suspend fun deleteAccount(code: String, deleteCreatedRecipes: Boolean): Result<Unit> =
        request { remoteDataSource.deleteAccount(code, deleteCreatedRecipes) }

    private suspend fun <T> request(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        Result.failure(error)
    }
}
