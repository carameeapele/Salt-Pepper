package com.example.saltpepper.data.profile

import com.example.saltpepper.core.network.ktor.KtorAppApi
import com.example.saltpepper.data.profile.datasource.ProfileRemoteDataSource
import com.example.saltpepper.data.profile.repository.ProfileRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ProfileRepositoryTest {
    @Test
    fun profileMappingPreservesNullableNameAndReportsInvalidDates() = runTest {
        var createdAt = "2026-11-10T14:10:33.803Z"
        val http = HttpClient(MockEngine { request ->
            respond(if (request.url.encodedPath == "/api/auth/email/verify") {
                """{"access_token":"access","user":{"id":"1","email":"user@example.test"}}"""
            } else {
                """{"display_name":null,"email":"user@example.test","created_at":"$createdAt"}"""
            })
        })
        try {
            val api = KtorAppApi(http)
            api.verifyEmailCode("user@example.test", "123456")
            val repository = ProfileRepositoryImpl(ProfileRemoteDataSource(api))
            val profile = repository.getProfile().getOrThrow()
            assertEquals(null, profile.displayName)
            assertEquals("user@example.test", profile.email)
            assertEquals("2026-11-10", profile.memberSince)
            createdAt = "not-a-date"
            assertTrue(repository.getProfile().isFailure)
        } finally {
            http.close()
        }
    }

    @Test
    fun cancellationIsNotConvertedToARepositoryFailure() = runTest {
        val http = HttpClient(MockEngine { request ->
            if (request.url.encodedPath == "/api/auth/email/verify") {
                respond("""{"access_token":"access","user":{"id":"1","email":"user@example.test"}}""")
            } else {
                throw CancellationException("Profile cancelled")
            }
        })
        try {
            val api = KtorAppApi(http)
            api.verifyEmailCode("user@example.test", "123456")
            val repository = ProfileRepositoryImpl(ProfileRemoteDataSource(api))
            assertFailsWith<CancellationException> { repository.getProfile() }
        } finally {
            http.close()
        }
    }
}
