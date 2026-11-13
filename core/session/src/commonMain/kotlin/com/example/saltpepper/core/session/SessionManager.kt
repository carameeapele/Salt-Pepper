package com.example.saltpepper.core.session

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Clock

data class SessionUser(val id: String, val email: String)

data class SessionCredentials(
    val user: SessionUser,
    val accessToken: String,
    val expiresInSeconds: Long
)

class SessionManager(
    private val persistentSession: PersistentSession? = null,
    private val nowMillis: () -> Long = { Clock.System.now().toEpochMilliseconds() }
) {
    private val mutex = Mutex()
    private val _user = MutableStateFlow<SessionUser?>(null)
    val user = _user.asStateFlow()
    var accessToken: String? = null
        private set
    private var accessExpiresAt = 0L

    // Keep refresh, cookie rotation and the consuming request in the same critical section.
    suspend fun <T> withSession(block: suspend () -> T): T = mutex.withLock { block() }

    fun refreshToken(): String? = persistentSession?.refreshToken()

    fun saveRefreshToken(token: String, expiresAt: Long) {
        persistentSession?.save(token, expiresAt)
    }

    fun accept(credentials: SessionCredentials): SessionUser {
        accessToken = credentials.accessToken
        accessExpiresAt = nowMillis() + credentials.expiresInSeconds.coerceAtLeast(0) * 1000
        _user.value = credentials.user
        return credentials.user
    }

    suspend fun refreshIfNeeded(refresh: suspend () -> SessionCredentials) {
        val refreshToken = refreshToken()
        if (persistentSession != null && refreshToken == null) {
            clear()
            error("No authenticated session is available")
        }
        if (nowMillis() >= accessExpiresAt - 30_000 && refreshToken != null) {
            withContext(NonCancellable) { accept(refresh()) }
        }
    }

    suspend fun restore(refresh: suspend () -> SessionCredentials): SessionUser? {
        if (refreshToken() == null) {
            clear()
            return null
        }
        persistentSession?.markActive()
        if (_user.value != null && accessToken != null && nowMillis() < accessExpiresAt - 30_000) {
            return _user.value
        }
        return withContext(NonCancellable) { accept(refresh()) }
    }

    fun clear() {
        try {
            persistentSession?.clear()
        } finally {
            accessToken = null
            accessExpiresAt = 0L
            _user.value = null
        }
    }
}
