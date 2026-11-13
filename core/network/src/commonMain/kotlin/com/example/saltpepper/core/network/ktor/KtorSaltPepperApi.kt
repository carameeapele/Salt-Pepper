package com.example.saltpepper.core.network.ktor

import com.example.saltpepper.core.network.BackendRequestException
import com.example.saltpepper.core.network.SaltPepperApi
import com.example.saltpepper.core.network.model.ProfileApiModel
import com.example.saltpepper.core.network.model.RecipeApiModel
import com.example.saltpepper.core.network.model.UserApiModel
import com.example.saltpepper.core.session.SessionCredentials
import com.example.saltpepper.core.session.SessionManager
import com.example.saltpepper.core.session.SessionUser
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.parseServerSetCookieHeader
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.put
import kotlin.time.Clock

class KtorSaltPepperApi(
    private val httpClient: HttpClient,
    config: ApiConfig = ApiConfig(),
    private val session: SessionManager = SessionManager(),
    private val nowMillis: () -> Long = { Clock.System.now().toEpochMilliseconds() }
) : SaltPepperApi {
    private val apiUrl = config.baseUrl.trimEnd('/')

    override suspend fun requestEmailCode(email: String) {
        authRequest("email/start", buildJsonObject { put("email", email) })
    }

    override suspend fun register(email: String, password: String) {
        authRequest("register", buildJsonObject {
            put("email", email)
            put("password", password)
        })
    }

    override suspend fun verifyEmailCode(email: String, code: String): UserApiModel =
        verify("email/verify", email, code)

    override suspend fun verifyRegistrationCode(email: String, code: String): UserApiModel =
        verify("register/verify", email, code)

    private suspend fun verify(endpoint: String, email: String, code: String): UserApiModel =
        session.withSession {
            withContext(NonCancellable) {
                session.accept(readCredentials(authRequest(endpoint, buildJsonObject {
                    put("email", email)
                    put("code", code)
                }))).asApiModel()
            }
        }

    override suspend fun restoreSession(): UserApiModel? = session.withSession {
        try {
            session.restore { readCredentials(authRequest("refresh")) }?.asApiModel()
        } catch (error: BackendRequestException) {
            if (error.status == 401) {
                session.clear()
                null
            } else {
                throw error
            }
        }
    }

    override suspend fun logout() = session.withSession {
        try {
            if (session.refreshToken() != null) authRequest("logout")
        } finally {
            session.clear()
        }
        Unit
    }

    override suspend fun getProfile(): ProfileApiModel = authenticated {
        val profile = readJsonResponse(httpClient.get("$apiUrl/me") { authorize() }) as? JsonObject
            ?: error("The backend returned an invalid profile")
        ProfileApiModel(
            displayName = profile.stringValue("display_name"),
            email = requireNotNull(profile.stringValue("email")) { "The backend returned no user email" },
            createdAt = requireNotNull(profile.stringValue("created_at")) {
                "The backend returned no membership date"
            }
        )
    }

    override suspend fun updateDisplayName(displayName: String): Unit = authenticated {
        readJsonResponse(httpClient.patch("$apiUrl/me") {
            authorize()
            jsonBody(buildJsonObject { put("display_name", displayName) })
        })
        Unit
    }

    override suspend fun requestAccountDeletionCode(): Unit = authenticated {
        authenticatedPost("me/delete/code")
    }

    override suspend fun deleteAccount(code: String, deleteCreatedRecipes: Boolean): Unit = authenticated {
        withContext(NonCancellable) {
            authenticatedPost("me/delete", buildJsonObject {
                put("code", code)
                put("delete_created_recipes", deleteCreatedRecipes)
            })
            session.clear()
        }
    }

    override suspend fun getMyRecipes(): List<RecipeApiModel> = recipes("recipes")

    override suspend fun getFavoriteRecipes(): List<RecipeApiModel> = recipes("recipes/favorites")

    private suspend fun recipes(endpoint: String): List<RecipeApiModel> = authenticated {
        val recipes = readJsonResponse(httpClient.get("$apiUrl/$endpoint") { authorize() }) as? JsonArray
            ?: error("The backend returned an invalid recipe list")
        recipes.map { element ->
            val recipe = element as? JsonObject ?: error("The backend returned an invalid recipe")
            RecipeApiModel(
                id = requireNotNull(recipe.stringValue("id")) { "The backend returned no recipe ID" },
                title = requireNotNull(recipe.stringValue("title")) { "The backend returned no recipe title" },
                totalTimeMinutes = (recipe["total_time_min"] as? JsonPrimitive)?.intOrNull
            )
        }
    }

    private suspend fun <T> authenticated(block: suspend () -> T): T = session.withSession {
        try {
            session.refreshIfNeeded { readCredentials(authRequest("refresh")) }
            requireNotNull(session.accessToken) { "No authenticated session is available" }
            block()
        } catch (error: BackendRequestException) {
            if (error.status == 401) session.clear()
            throw error
        }
    }

    private suspend fun authenticatedPost(endpoint: String, payload: JsonObject? = null) {
        readJsonResponse(httpClient.post("$apiUrl/$endpoint") {
            authorize()
            payload?.let { jsonBody(it) }
        })
    }

    private fun HttpRequestBuilder.authorize() {
        val token = requireNotNull(session.accessToken) { "No authenticated session is available" }
        headers.append(HttpHeaders.Authorization, "Bearer $token")
    }

    private fun HttpRequestBuilder.jsonBody(payload: JsonObject) {
        contentType(ContentType.Application.Json)
        setBody(payload.toString())
    }

    private suspend fun authRequest(endpoint: String, payload: JsonObject? = null): JsonElement? {
        val response = httpClient.post("$apiUrl/auth/$endpoint") {
            session.refreshToken()?.let { headers.append(HttpHeaders.Cookie, "sp_refresh=$it") }
            payload?.let { jsonBody(it) }
        }
        return readJsonResponse(response)
    }

    private fun readCredentials(body: JsonElement?): SessionCredentials {
        val response = body as? JsonObject ?: error("The backend returned no access token")
        val data = response["data"] as? JsonObject
        val profile = response["user"] as? JsonObject
            ?: data?.get("user") as? JsonObject
            ?: response.takeIf { it.stringValue("email") != null }
            ?: data?.takeIf { it.stringValue("email") != null }
        val token = response.stringValue("access_token")
            ?: response.stringValue("token")
            ?: response.stringValue("access")
            ?: data?.stringValue("access_token")
            ?: data?.stringValue("token")
        require(!token.isNullOrBlank()) { "The backend returned no access token" }
        return SessionCredentials(
            user = SessionUser(
                id = requireNotNull(profile?.stringValue("id")) { "The backend returned no user ID" },
                email = requireNotNull(profile.stringValue("email")) { "The backend returned no user email" }
            ),
            accessToken = token,
            expiresInSeconds = (response["expires_in"] as? JsonPrimitive)?.content?.toLongOrNull() ?: 0L
        )
    }

    private suspend fun readJsonResponse(response: HttpResponse): JsonElement? {
        if (response.status.value in 200..299) {
            response.headers.getAll(HttpHeaders.SetCookie)?.forEach { header ->
                val cookie = parseServerSetCookieHeader(header)
                if (cookie.name == "sp_refresh") {
                    val now = nowMillis()
                    val expiresAt = cookie.maxAge?.let { now + it.toLong() * 1000 }
                        ?: cookie.expires?.timestamp
                        ?: (now + 14L * 24 * 60 * 60 * 1000)
                    session.saveRefreshToken(cookie.value, expiresAt)
                }
            }
        }
        val body = response.bodyAsText()
        val parsed = try {
            Json.parseToJsonElement(body)
        } catch (error: SerializationException) {
            if (response.status.value in 200..299 && body.isNotBlank()) throw error
            null
        }
        if (response.status.value !in 200..299) {
            val errorBody = parsed as? JsonObject
            val message = errorBody?.get("error")?.errorMessage()
                ?: errorBody?.get("detail")?.errorMessage()
                ?: errorBody?.entries?.joinToString("\n") { (field, value) ->
                    "$field: ${value.errorMessage()}"
                }?.takeIf { it.isNotBlank() }
                ?: "Backend request failed (HTTP ${response.status.value})"
            throw BackendRequestException(response.status.value, message)
        }
        return parsed
    }

    private fun SessionUser.asApiModel() = UserApiModel(id, email)

    private fun JsonObject.stringValue(name: String): String? =
        (get(name) as? JsonPrimitive)?.takeIf { it.isString }?.content?.takeIf { it.isNotBlank() }

    private fun JsonElement.errorMessage(): String = when (this) {
        is JsonPrimitive -> content
        is JsonArray -> joinToString("; ") { it.errorMessage() }
        else -> toString()
    }
}
