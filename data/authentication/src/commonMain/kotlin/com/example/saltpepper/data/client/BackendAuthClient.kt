package com.example.saltpepper.data.client

import com.example.saltpepper.data.model.User
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

class BackendAuthClient(
    private val httpClient: HttpClient,
    baseUrl: String = "https://getsaltpepper.fr/api"
) {
    private val authUrl = "${baseUrl.trimEnd('/')}/auth/email"
    var accessToken: String? = null
        private set
    var currentUser: User? = null
        private set

    suspend fun requestEmailCode(email: String) {
        request("start", buildJsonObject { put("email", email) })
    }

    suspend fun verifyEmailCode(email: String, code: String): User {
        val response = request("verify", buildJsonObject {
            put("email", email)
            put("code", code)
        })
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
        val user = User(
            id = profile?.stringValue("id") ?: email,
            email = profile?.stringValue("email") ?: email
        )
        accessToken = token
        currentUser = user
        return user
    }

    suspend fun logout() {
        accessToken = null
        currentUser = null
    }

    private suspend fun request(endpoint: String, payload: JsonObject): JsonObject {
        val response = httpClient.post("$authUrl/$endpoint") {
            contentType(ContentType.Application.Json)
            setBody(payload.toString())
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
            error(message)
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