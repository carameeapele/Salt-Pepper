package com.example.saltpepper.data.client

import com.example.saltpepper.data.repository.BackendProfileRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProfileRequestsTest {
    @Test
    fun profileAndRecipeListsUseAuthenticatedGetAndParseArrayResponses() = runTest {
        val paths = mutableListOf<String>()
        val http = HttpClient(MockEngine { request ->
            val path = request.url.encodedPath
            paths += path
            val body = if (path == "/api/auth/email/verify") {
                """{"access_token":"synthetic-access","expires_in":3600,"user":{"id":"1","email":"user@example.test"}}"""
            } else {
                assertEquals(HttpMethod.Get, request.method)
                assertEquals("Bearer synthetic-access", request.headers[HttpHeaders.Authorization])
                when (path) {
                    "/api/me" ->
                        """{"display_name":"Camille","email":"user@example.test","created_at":"2026-11-10T14:10:33.803Z"}"""
                    "/api/recipes/favorites" -> "[]"
                    "/api/recipes" -> """[{"id":"recipe-1","title":"Soup","total_time_min":20}]"""
                    else -> error("Unexpected endpoint: $path")
                }
            }
            respond(body, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        })
        try {
            val client = BackendAuthClient(http)
            client.verifyEmailCode("user@example.test", "123456")
            val repository = BackendProfileRepository(client)
            val profile = repository.getProfile().getOrThrow()
            assertEquals("Camille", profile.displayName)
            assertEquals("2026-11-10", profile.memberSince)
            assertEquals(emptyList(), repository.getFavorites().getOrThrow())
            val recipe = repository.getMyRecipes().getOrThrow().single()
            assertEquals("recipe-1", recipe.id)
            assertEquals("Soup", recipe.title)
            assertEquals(20, recipe.totalTimeMinutes)
            assertEquals(
                listOf("/api/auth/email/verify", "/api/me", "/api/recipes/favorites", "/api/recipes"),
                paths
            )
        } finally {
            http.close()
        }
    }

    @Test
    fun failuresAndMalformedListsAreNotReportedAsEmptyLists() = runTest {
        var fail = true
        val http = HttpClient(MockEngine { request ->
            when (request.url.encodedPath) {
                "/api/auth/email/verify" -> respond(
                    """{"access_token":"synthetic-access","user":{"id":"1","email":"user@example.test"}}"""
                )
                else -> if (fail) {
                    respond("""{"detail":"Unavailable"}""", HttpStatusCode.ServiceUnavailable)
                } else {
                    respond("""{"unexpected":"object"}""")
                }
            }
        })
        try {
            val client = BackendAuthClient(http)
            client.verifyEmailCode("user@example.test", "123456")
            val repository = BackendProfileRepository(client)
            assertEquals("Unavailable", repository.getFavorites().exceptionOrNull()?.message)
            fail = false
            assertTrue(repository.getMyRecipes().isFailure)
        } finally {
            http.close()
        }
    }
}
