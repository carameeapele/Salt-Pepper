package com.example.saltpepper.data.client

import com.example.saltpepper.data.session.PersistentSession
import com.example.saltpepper.data.session.SecureSessionStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
import kotlin.test.assertSame

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
            val recreated = BackendAuthClient(fixture.httpClient, baseUrl, recreatedSession) { fixture.now }
            assertNull(recreated.accessToken)
            assertNull(recreated.currentUser)
            val restored = assertNotNull(recreated.restoreSession())
            assertEquals("synthetic-user", restored.id)
            assertEquals("tester@example.test", restored.email)
            assertSame(restored, recreated.currentUser)
            assertEquals("synthetic-access", recreated.accessToken)
            assertEquals("synthetic-new", recreatedSession.refreshToken())
            assertEquals(
                "synthetic-new",
                PersistentSession(fixture.store) { fixture.now }.refreshToken()
            )
            assertEquals(2, requests)

            fixture.now += 89_999
            assertSame(restored, recreated.restoreSession())
            assertEquals(2, requests)

            fixture.now += 30_001
            assertEquals(restored, recreated.restoreSession())
            assertEquals("synthetic-access-renewed", recreated.accessToken)
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
            assertNull(fixture.client.accessToken)
            assertNull(fixture.client.currentUser)
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
            assertNull(fixture.client.currentUser)
            assertNull(fixture.client.accessToken)
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
            assertNull(fixture.client.currentUser)
            assertNull(fixture.client.accessToken)
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
            assertNull(fixture.client.currentUser)
            assertNull(fixture.client.accessToken)
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
            users.forEach { assertSame(user, it) }
            assertSame(user, fixture.client.currentUser)
            assertEquals("synthetic-access", fixture.client.accessToken)
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
            assertNull(fixture.client.accessToken)
            assertNull(fixture.client.currentUser)
            assertNull(fixture.client.restoreSession())
            fixture.client.logout()
            assertEquals(2, requests)
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
        val httpClient = HttpClient(engine)
        val client = BackendAuthClient(httpClient, baseUrl, session) { now }

        fun seedCookie() = session.save(Cookie("sp_refresh", "synthetic-old", maxAge = refreshMaxAge))

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