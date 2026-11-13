package com.example.saltpepper.data.client

import com.example.saltpepper.core.network.BackendRequestException
import com.example.saltpepper.core.network.ktor.ApiConfig
import com.example.saltpepper.core.network.ktor.KtorAppApi
import com.example.saltpepper.core.session.PersistentSession
import com.example.saltpepper.core.session.SecureSessionStore
import com.example.saltpepper.core.session.SessionManager
import com.example.saltpepper.data.authentication.datasource.AuthenticationRemoteDataSource
import com.example.saltpepper.data.authentication.repository.AuthRepositoryImpl
import com.example.saltpepper.data.authentication.repository.AuthState
import com.example.saltpepper.data.profile.datasource.ProfileRemoteDataSource
import com.example.saltpepper.data.profile.repository.ProfileRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SessionRestorationTest {
    private val baseUrl = "https://auth.example.test/api"
    private val refreshMaxAge = 30 * 86400
    private val sessionBody =
        """{"access_token":"synthetic-access","expires_in":120,"user":{"id":"synthetic-user","email":"tester@example.test"}}"""
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

    @Test
    fun loginPersistsCookieAndRecreatedClientRestoresRotatesAndCachesSession() = runTest {
        var requests = 0
        val engine = MockEngine { request ->
            requests++
            assertEquals(HttpMethod.Post, request.method)
            when (requests) {
                1 -> {
                    assertEquals("$baseUrl/auth/email/verify", request.url.toString())
                    assertNull(request.headers[HttpHeaders.Cookie])
                    val body = Json.parseToJsonElement(
                        (request.body as io.ktor.http.content.TextContent).text
                    ).jsonObject
                    assertEquals("tester@example.test", body.getValue("email").jsonPrimitive.content)
                    assertEquals("123456", body.getValue("code").jsonPrimitive.content)
                    respond(sessionBody, headers = cookieHeaders("synthetic-old"))
                }
                2 -> {
                    request.assertEndpointAndCookie("refresh", "synthetic-old")
                    respond(sessionBody, headers = cookieHeaders("synthetic-new"))
                }
                3 -> {
                    request.assertEndpointAndCookie("refresh", "synthetic-new")
                    respond(
                        sessionBody.replace("synthetic-access", "synthetic-access-renewed"),
                        headers = jsonHeaders
                    )
                }
                else -> error("Unexpected HTTP request: ${request.url}")
            }
        }
        val fixture = Fixture(engine)
        try {
            fixture.client.verifyEmailCode("tester@example.test", "123456")
            assertEquals("synthetic-old", fixture.session.refreshToken())
            val persisted = Json.parseToJsonElement(assertNotNull(fixture.store.read())).jsonObject
            assertEquals("synthetic-old", persisted.getValue("token").jsonPrimitive.content)
            assertEquals(
                fixture.now + refreshMaxAge.toLong() * 1000,
                persisted.getValue("expiresAt").jsonPrimitive.long
            )

            val recreatedSession = PersistentSession(fixture.store) { fixture.now }
            val recreatedManager = SessionManager(recreatedSession) { fixture.now }
            val recreated =
                KtorAppApi(fixture.httpClient, ApiConfig(baseUrl), recreatedManager) { fixture.now }
            assertNull(recreatedManager.accessToken)
            assertNull(recreatedManager.user.value)
            val restored = assertNotNull(recreated.restoreSession())
            assertEquals("synthetic-user", restored.id)
            assertEquals("tester@example.test", restored.email)
            assertEquals(restored.id, recreatedManager.user.value?.id)
            assertEquals("synthetic-access", recreatedManager.accessToken)
            assertEquals("synthetic-new", recreatedSession.refreshToken())
            assertEquals(
                "synthetic-new",
                PersistentSession(fixture.store) { fixture.now }.refreshToken()
            )
            assertEquals(2, requests)

            fixture.now += 89_999
            assertEquals(restored, recreated.restoreSession())
            assertEquals(2, requests)

            fixture.now += 30_001
            assertEquals(restored, recreated.restoreSession())
            assertEquals("synthetic-access-renewed", recreatedManager.accessToken)
            assertEquals(3, requests)
        } finally {
            fixture.close()
        }
    }

    @Test
    fun restoreWithoutStoredCookieDoesNotRequest() = runTest {
        var requests = 0
        val fixture = Fixture(MockEngine {
            requests++
            error("Restore without a cookie must not request")
        })
        try {
            assertNull(fixture.client.restoreSession())
            assertNull(fixture.manager.accessToken)
            assertNull(fixture.manager.user.value)
            assertNull(fixture.store.read())
            assertEquals(0, requests)
        } finally {
            fixture.close()
        }
    }

    @Test
    fun unauthorizedRefreshClearsStoredCookieAndExistingUser() = runTest {
        var requests = 0
        val fixture = Fixture(MockEngine { request ->
            request.assertEndpointAndCookie("refresh", "synthetic-old")
            requests++
            if (requests == 1) {
                respond(sessionBody, headers = jsonHeaders)
            } else {
                respond("""{"detail":"Session revoked"}""", HttpStatusCode.Unauthorized, jsonHeaders)
            }
        })
        try {
            fixture.seedCookie()
            assertNotNull(fixture.client.restoreSession())
            fixture.now += 120_000
            assertNull(fixture.client.restoreSession())
            assertNull(fixture.manager.user.value)
            assertNull(fixture.manager.accessToken)
            assertNull(fixture.session.refreshToken())
            assertNull(fixture.store.read())
            assertNull(fixture.client.restoreSession())
            assertEquals(2, requests)
        } finally {
            fixture.close()
        }
    }

    @Test
    fun serviceUnavailableRefreshRetainsStoredCookie() = runTest {
        var requests = 0
        val fixture = Fixture(MockEngine { request ->
            request.assertEndpointAndCookie("refresh", "synthetic-old")
            requests++
            respond("""{"detail":"Try later"}""", HttpStatusCode.ServiceUnavailable, jsonHeaders)
        })
        try {
            fixture.seedCookie()
            val stored = fixture.store.read()
            val failure = assertFailsWith<BackendRequestException> { fixture.client.restoreSession() }
            assertEquals(503, failure.status)
            assertEquals(stored, fixture.store.read())
            assertEquals("synthetic-old", fixture.session.refreshToken())
            assertNull(fixture.manager.user.value)
            assertNull(fixture.manager.accessToken)
            assertEquals(1, requests)
        } finally {
            fixture.close()
        }
    }

    @Test
    fun networkFailureDuringRefreshRetainsStoredCookie() = runTest {
        var requests = 0
        val networkFailure = SyntheticNetworkFailure()
        val fixture = Fixture(MockEngine { request ->
            request.assertEndpointAndCookie("refresh", "synthetic-old")
            requests++
            throw networkFailure
        })
        try {
            fixture.seedCookie()
            val stored = fixture.store.read()
            assertFailsWith<SyntheticNetworkFailure> { fixture.client.restoreSession() }
            assertEquals(stored, fixture.store.read())
            assertEquals("synthetic-old", fixture.session.refreshToken())
            assertNull(fixture.manager.user.value)
            assertNull(fixture.manager.accessToken)
            assertEquals(1, requests)
        } finally {
            fixture.close()
        }
    }

    @Test
    fun concurrentRestoresShareOneRefreshWithPositiveAccessLifetime() = runTest {
        var requests = 0
        val entered = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val fixture = Fixture(MockEngine { request ->
            request.assertEndpointAndCookie("refresh", "synthetic-old")
            requests++
            entered.complete(Unit)
            release.await()
            respond(sessionBody, headers = cookieHeaders("synthetic-new"))
        })
        try {
            fixture.seedCookie()
            val restores = List(10) { async { fixture.client.restoreSession() } }
            entered.await()
            assertEquals(1, requests)
            release.complete(Unit)
            val users = restores.awaitAll()
            val user = assertNotNull(users.first())
            users.forEach { assertEquals(user, it) }
            assertEquals(user.id, fixture.manager.user.value?.id)
            assertEquals("synthetic-access", fixture.manager.accessToken)
            assertEquals("synthetic-new", fixture.session.refreshToken())
            assertEquals(1, requests)
        } finally {
            release.complete(Unit)
            fixture.close()
        }
    }

    @Test
    fun logoutSendsCookieAndClearsSession() = runTest {
        verifyLogoutCleanup()
    }

    @Test
    fun logoutClearsSessionDespiteServerFailure() = runTest {
        verifyLogoutCleanup(serverFailure = true)
    }

    @Test
    fun logoutClearsSessionDespiteNetworkFailure() = runTest {
        verifyLogoutCleanup(networkFailure = true)
    }

    private suspend fun verifyLogoutCleanup(serverFailure: Boolean = false, networkFailure: Boolean = false) {
        var requests = 0
        val fixture = Fixture(MockEngine { request ->
            requests++
            if (requests == 1) {
                request.assertEndpointAndCookie("refresh", "synthetic-old")
                respond(sessionBody, headers = cookieHeaders("synthetic-new"))
            } else {
                request.assertEndpointAndCookie("logout", "synthetic-new")
                when {
                    networkFailure -> throw SyntheticNetworkFailure()
                    serverFailure -> respond(
                        """{"detail":"Try later"}""", HttpStatusCode.ServiceUnavailable, jsonHeaders
                    )
                    else -> respond("", HttpStatusCode.NoContent)
                }
            }
        })
        try {
            fixture.seedCookie()
            assertNotNull(fixture.client.restoreSession())
            when {
                networkFailure -> assertFailsWith<SyntheticNetworkFailure> { fixture.client.logout() }
                serverFailure -> {
                    val failure = assertFailsWith<BackendRequestException> { fixture.client.logout() }
                    assertEquals(503, failure.status)
                }
                else -> fixture.client.logout()
            }
            assertNull(fixture.store.read())
            assertNull(fixture.session.refreshToken())
            assertNull(fixture.manager.accessToken)
            assertNull(fixture.manager.user.value)
            assertNull(fixture.client.restoreSession())
            fixture.client.logout()
            assertEquals(2, requests)
        } finally {
            fixture.close()
        }
    }

    @Test
    fun deletionRequestsCodeThenClearsSessionOnlyAfterConfirmedDeletion() = runTest {
        var requests = 0
        val fixture = Fixture(MockEngine { request ->
            requests++
            if (requests == 1) {
                request.assertEndpointAndCookie("refresh", "synthetic-old")
                respond(sessionBody, headers = jsonHeaders)
            } else {
                assertEquals(HttpMethod.Post, request.method)
                assertEquals("Bearer synthetic-access", request.headers[HttpHeaders.Authorization])
                when (requests) {
                    2 -> {
                        assertEquals("$baseUrl/me/delete/code", request.url.toString())
                        respond("", HttpStatusCode.Accepted)
                    }
                    3 -> {
                        assertEquals("$baseUrl/me/delete", request.url.toString())
                        val payload = Json.parseToJsonElement(
                            (request.body as io.ktor.http.content.TextContent).text
                        ).jsonObject
                        assertEquals("462947", payload["code"]?.jsonPrimitive?.content)
                        assertEquals("true", payload["delete_created_recipes"]?.jsonPrimitive?.content)
                        respond("", HttpStatusCode.NoContent)
                    }
                    else -> error("Unexpected request")
                }
            }
        })
        try {
            fixture.seedCookie()
            val repository = AuthRepositoryImpl(AuthenticationRemoteDataSource(fixture.client), fixture.manager, backgroundScope)
            val profileRepository = ProfileRepositoryImpl(ProfileRemoteDataSource(fixture.client))
            repository.restoreSession().getOrThrow()
            profileRepository.requestAccountDeletionCode().getOrThrow()
            assertNotNull(fixture.store.read())
            assertNotNull(fixture.manager.user.value)
            profileRepository.deleteAccount("462947", true).getOrThrow()
            runCurrent()
            assertEquals(AuthState.Unauthenticated, repository.authState.value)
            assertNull(fixture.store.read())
            assertNull(fixture.manager.accessToken)
            assertNull(fixture.manager.user.value)
            assertNull(repository.restoreSession().getOrThrow())
            assertEquals(3, requests)
        } finally {
            fixture.close()
        }
    }

    @Test
    fun failedDeletionPreservesSessionAndCanBeRetriedWithoutDeletingRecipes() = runTest {
        var requests = 0
        val fixture = Fixture(MockEngine { request ->
            requests++
            if (requests == 1) {
                respond(sessionBody, headers = jsonHeaders)
            } else {
                assertEquals("$baseUrl/me/delete", request.url.toString())
                val payload = Json.parseToJsonElement(
                    (request.body as io.ktor.http.content.TextContent).text
                ).jsonObject
                assertEquals("false", payload["delete_created_recipes"]?.jsonPrimitive?.content)
                if (requests == 2) {
                    respond("""{"detail":"Invalid or expired code"}""", HttpStatusCode.BadRequest, jsonHeaders)
                } else {
                    respond("", HttpStatusCode.NoContent)
                }
            }
        })
        try {
            fixture.seedCookie()
            val repository = AuthRepositoryImpl(AuthenticationRemoteDataSource(fixture.client), fixture.manager, backgroundScope)
            val profileRepository = ProfileRepositoryImpl(ProfileRemoteDataSource(fixture.client))
            val user = assertNotNull(repository.restoreSession().getOrThrow())
            val stored = fixture.store.read()
            val result = profileRepository.deleteAccount("000000", false)
            runCurrent()
            assertEquals("Invalid or expired code", result.exceptionOrNull()?.message)
            assertEquals(AuthState.Authenticated(user), repository.authState.value)
            assertEquals(stored, fixture.store.read())
            assertNotNull(fixture.manager.accessToken)
            assertEquals(user.id, fixture.manager.user.value?.id)
            profileRepository.deleteAccount("462947", false).getOrThrow()
            runCurrent()
            assertNull(fixture.store.read())
            assertEquals(AuthState.Unauthenticated, repository.authState.value)
            assertEquals(3, requests)
        } finally {
            fixture.close()
        }
    }

    @Test
    fun profileSessionRejectionUpdatesAuthenticationStateAndClearsCredentials() = runTest {
        val fixture = Fixture(MockEngine { request ->
            if (request.url.encodedPath == "/api/auth/refresh") {
                respond(sessionBody, headers = jsonHeaders)
            } else {
                respond("""{"detail":"Session revoked"}""", HttpStatusCode.Unauthorized, jsonHeaders)
            }
        })
        try {
            fixture.seedCookie()
            val repository = AuthRepositoryImpl(
                AuthenticationRemoteDataSource(fixture.client), fixture.manager, backgroundScope
            )
            val profileRepository = ProfileRepositoryImpl(ProfileRemoteDataSource(fixture.client))
            val user = assertNotNull(repository.restoreSession().getOrThrow())
            runCurrent()
            assertEquals(AuthState.Authenticated(user), repository.authState.value)
            assertEquals("Session revoked", profileRepository.getProfile().exceptionOrNull()?.message)
            runCurrent()
            assertEquals(AuthState.Unauthenticated, repository.authState.value)
            assertNull(fixture.store.read())
            assertNull(fixture.manager.accessToken)
        } finally {
            fixture.close()
        }
    }

    private fun cookieHeaders(token: String) = headersOf(
        HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString()),
        HttpHeaders.SetCookie to listOf("sp_refresh=$token; Max-Age=$refreshMaxAge; Path=/api/auth; HttpOnly; Secure")
    )

    private fun HttpRequestData.assertEndpointAndCookie(endpoint: String, token: String) {
        assertEquals(HttpMethod.Post, method)
        assertEquals("$baseUrl/auth/$endpoint", url.toString())
        assertEquals("sp_refresh=$token", headers[HttpHeaders.Cookie])
    }

    private inner class Fixture(engine: MockEngine) {
        var now = 1_000_000L
        val store = MemoryStore()
        val session = PersistentSession(store) { now }
        val manager = SessionManager(session) { now }
        val httpClient = HttpClient(engine)
        val client = KtorAppApi(httpClient, ApiConfig(baseUrl), manager) { now }

        fun seedCookie() = session.save("synthetic-old", now + refreshMaxAge.toLong() * 1000)

        fun close() = httpClient.close()
    }

    private class MemoryStore : SecureSessionStore {
        private var value: String? = null

        override fun read(): String? = value

        override fun write(value: String) {
            this.value = value
        }

        override fun clear() {
            value = null
        }
    }

    private class SyntheticNetworkFailure : Exception("Synthetic network unavailable")
}