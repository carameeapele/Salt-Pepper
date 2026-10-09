package com.example.saltpepper.data.session

import io.ktor.http.Cookie
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.time.Clock

class PersistentSession(
    private val store: SecureSessionStore,
    private val nowMillis: () -> Long = { Clock.System.now().toEpochMilliseconds() }
) {
    private val inactivityMillis = 14L * 24 * 60 * 60 * 1000

    fun refreshToken(): String? {
        val stored = store.read() ?: return null
        val record = try {
            Json.parseToJsonElement(stored).jsonObject
        } catch (_: Exception) {
            clear()
            return null
        }
        val token = record["token"]?.jsonPrimitive?.content
        val expiresAt = record["expiresAt"]?.jsonPrimitive?.content?.toLongOrNull()
        val lastUsedAt = record["lastUsedAt"]?.jsonPrimitive?.content?.toLongOrNull()
        val now = nowMillis()
        if (token.isNullOrBlank() || expiresAt == null || lastUsedAt == null ||
            now >= expiresAt || now - lastUsedAt >= inactivityMillis || now < lastUsedAt
        ) {
            clear()
            return null
        }
        return token
    }

    fun save(cookie: Cookie) {
        val now = nowMillis()
        val expiresAt = cookie.maxAge?.let { now + it.toLong() * 1000 }
            ?: cookie.expires?.timestamp
            ?: (now + inactivityMillis)
        if (cookie.value.isBlank() || expiresAt <= now) {
            clear()
            return
        }
        store.write(buildJsonObject {
            put("token", cookie.value)
            put("expiresAt", expiresAt)
            put("lastUsedAt", now)
        }.toString())
    }

    fun markActive() {
        if (refreshToken() == null) return
        val record = Json.parseToJsonElement(store.read()!!).jsonObject
        store.write(buildJsonObject {
            put("token", record.getValue("token"))
            put("expiresAt", record.getValue("expiresAt"))
            put("lastUsedAt", nowMillis())
        }.toString())
    }

    fun clear() = store.clear()
}