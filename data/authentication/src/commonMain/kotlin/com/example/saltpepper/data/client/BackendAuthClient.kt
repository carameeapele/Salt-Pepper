package com.example.saltpepper.data.client

import com.example.saltpepper.data.model.User
import com.example.saltpepper.data.session.PersistentSession
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.parseServerSetCookieHeader
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlin.time.Clock

class BackendAuthClient(
    private val httpClient: HttpClient,
    baseUrl: String = "https://getsaltpepper.fr/api",
    private val session: PersistentSession? = null,
    private val nowMillis: () -> Long = { Clock.System.now().toEpochMilliseconds() }
) {
    private val authUrl = "${baseUrl.trimEnd('/')}/auth"
    private val sessionMutex = Mutex()
    private var accessExpiresAt = 0L
    var accessToken: String? = null
        private set
    var currentUser: User? = null
        private set

    suspend fun requestEmailCode(email: String) {
        request("email/start", buildJsonObject { put("email", email) })
    }

    suspend fun verifyEmailCode(email: String, code: String): User = sessionMutex.withLock {
        val response = request("email/verify", buildJsonObject {
            put("email", email)
            put("code", code)
        })
        readSession(response)
    }

    suspend fun restoreSession(): User? = sessionMutex.withLock {
        if (session?.refreshToken() == null) {
            clearSession()
            return@withLock null
        }
        session.markActive()
        if (currentUser != null && accessToken != null && nowMillis() < accessExpiresAt - 30_000) {
            return@withLock currentUser
        }
        try {
            withContext(NonCancellable) {
                readSession(request("refresh"))
            }
        } catch (error: BackendRequestException) {
            if (error.status == 401) {
                clearSession()
                null
            } else {
                throw error
            }
        }
    }

    private fun readSession(response: JsonObject): User {
        val data = response["data"] as? JsonObject
        val profile = response["user"]?.jsonObject
            ?: data?.get("user")?.jsonObject
            ?: response.takeIf { it.stringValue("email") != null }
            ?: data?.takeIf { it.stringValue("email") != null }
        val token = response.stringValue("access_token")
            ?: response.stringValue("token")
            ?: response.stringValue("access")
            ?: data?.stringValue("access_token")
            ?: data?.stringValue("token")
        require(!token.isNullOrBlank()) { "The backend returned no access token" }
        val user = User(
            id = requireNotNull(profile?.stringValue("id")) { "The backend returned no user ID" },
            email = requireNotNull(profile?.stringValue("email")) { "The backend returned no user email" }
        )
        val expiresIn = response["expires_in"]?.let { (it as? JsonPrimitive)?.content?.toLongOrNull() }
            ?: 0L
        accessToken = token
        accessExpiresAt = nowMillis() + expiresIn.coerceAtLeast(0) * 1000
        currentUser = user
        return user
    }

    suspend fun logout() = sessionMutex.withLock {
        try {
            if (session?.refreshToken() != null) request("logout")
        } finally {
            clearSession()
        }
    }

    private fun clearSession() {
        session?.clear()
        accessToken = null
        accessExpiresAt = 0L
        currentUser = null
    }

    private suspend fun request(endpoint: String, payload: JsonObject? = null): JsonObject {
        val response = httpClient.post("$authUrl/$endpoint") {
            session?.refreshToken()?.let { headers.append(HttpHeaders.Cookie, "sp_refresh=$it") }
            if (payload != null) {
                contentType(ContentType.Application.Json)
                setBody(payload.toString())
            }
        }
        if (response.status.value in 200..299) {
            response.headers.getAll(HttpHeaders.SetCookie)?.forEach { header ->
                val cookie = parseServerSetCookieHeader(header)
                if (cookie.name == "sp_refresh") session?.save(cookie)
            }
        }
        val body = response.bodyAsText()
        val parsed = runCatching { Json.parseToJsonElement(body) as? JsonObject }.getOrNull()
        if (response.status.value !in 200..299) {
            val message = parsed?.get("error")?.errorMessage()
                ?: parsed?.get("detail")?.errorMessage()
                ?: parsed?.entries?.joinToString("\n") { (field, value) ->
                    "$field: ${value.errorMessage()}"
                }?.takeIf { it.isNotBlank() }
                ?: "Backend request failed (HTTP ${response.status.value})"
            throw BackendRequestException(response.status.value, message)
        }
        return parsed ?: if (response.status.value in 200..299) buildJsonObject {} else {
            error("The backend returned an invalid JSON response")
        }
    }

    private fun JsonObject.stringValue(name: String): String? =
        (get(name) as? JsonPrimitive)?.takeIf { it.isString }?.content?.takeIf { it.isNotBlank() }

    private fun JsonElement.errorMessage(): String = when (this) {
        is JsonPrimitive -> content
        is JsonArray -> joinToString("; ") { it.errorMessage() }
        else -> toString()
    }
}

class BackendRequestException(val status: Int, message: String) : Exception(message)