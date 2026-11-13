package com.example.saltpepper.core.network

import com.example.saltpepper.core.network.ktor.KtorSaltPepperApi
import com.example.saltpepper.core.session.PersistentSession
import com.example.saltpepper.core.session.SecureSessionStore
import com.example.saltpepper.core.session.SessionCredentials
import com.example.saltpepper.core.session.SessionManager
import com.example.saltpepper.core.session.SessionUser
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class SharedSessionRequestsTest {
    private val sessionBody =
        """{"access_token":"renewed-access","expires_in":120,"user":{"id":"1","email":"user@example.test"}}"""

    @Test
    fun profileAndRecipeRequestsShareOneRefreshAndUseTheRenewedAccessToken() = runTest {
        val store = MemoryStore()
        val persistent = PersistentSession(store) { 0L }
        persistent.save("old-refresh", 86400_000L)
        val session = SessionManager(persistent) { 0L }
        var refreshes = 0
        var requests = 0
        val entered = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        val http = HttpClient(MockEngine { request ->
            when (request.url.encodedPath) {
                "/api/auth/refresh" -> {
                    refreshes++
                    assertEquals("sp_refresh=old-refresh", request.headers[HttpHeaders.Cookie])
                    entered.complete(Unit)
                    release.await()
                    respond(sessionBody, headers = headersOf(
                        HttpHeaders.SetCookie, "sp_refresh=new-refresh; Max-Age=86400; Path=/api/auth"
                    ))
                }
                else -> {
                    requests++
                    assertEquals("Bearer renewed-access", request.headers[HttpHeaders.Authorization])
                    respond(if (request.url.encodedPath == "/api/me") {
                        """{"email":"user@example.test","created_at":"2026-11-10T00:00:00Z"}"""
                    } else "[]")
                }
            }
        })
        try {
            val api = KtorSaltPepperApi(http, session = session, nowMillis = { 0L })
            val calls = List(10) { index ->
                async { if (index % 2 == 0) api.getProfile() else api.getMyRecipes() }
            }
            entered.await()
            assertEquals(1, refreshes)
            release.complete(Unit)
            calls.awaitAll()
            assertEquals(1, refreshes)
            assertEquals(10, requests)
            assertEquals("new-refresh", persistent.refreshToken())
        } finally {
            release.complete(Unit)
            http.close()
        }
    }

    @Test
    fun cancellationDuringRefreshPersistsRotationWithoutReplayingTheOldCookie() = runTest {
        val store = MemoryStore()
        val persistent = PersistentSession(store) { 0L }
        persistent.save("old-refresh", 86400_000L)
        val session = SessionManager(persistent) { 0L }
        val entered = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()
        var refreshes = 0
        val http = HttpClient(MockEngine { request ->
            assertEquals("/api/auth/refresh", request.url.encodedPath)
            refreshes++
            entered.complete(Unit)
            release.await()
            respond(sessionBody, headers = headersOf(
                HttpHeaders.SetCookie, "sp_refresh=new-refresh; Max-Age=86400; Path=/api/auth"
            ))
        })
        try {
            val api = KtorSaltPepperApi(http, session = session, nowMillis = { 0L })
            val restore = async { api.restoreSession() }
            entered.await()
            restore.cancel()
            release.complete(Unit)
            assertFailsWith<CancellationException> { restore.await() }
            assertEquals("new-refresh", persistent.refreshToken())
            assertEquals("1", api.restoreSession()?.id)
            assertEquals(1, refreshes)
        } finally {
            release.complete(Unit)
            http.close()
        }
    }

    @Test
    fun expiredRefreshCredentialClearsTheSessionWithoutSendingAnAuthenticatedRequest() = runTest {
        var now = 0L
        val persistent = PersistentSession(MemoryStore()) { now }
        persistent.save("refresh", 1000)
        val session = SessionManager(persistent) { now }
        session.accept(SessionCredentials(SessionUser("1", "user@example.test"), "access", 3600))
        var requests = 0
        val http = HttpClient(MockEngine {
            requests++
            error("Expired credentials must not be sent")
        })
        try {
            now = 1000
            assertFailsWith<IllegalStateException> {
                KtorSaltPepperApi(http, session = session).getProfile()
            }
            assertEquals(0, requests)
            assertNull(session.user.value)
            assertNull(session.accessToken)
        } finally {
            http.close()
        }
    }

    private class MemoryStore : SecureSessionStore {
        private var value: String? = null
        override fun read() = value
        override fun write(value: String) { this.value = value }
        override fun clear() { value = null }
    }
}
