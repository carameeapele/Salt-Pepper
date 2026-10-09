package com.example.saltpepper.data.session

import io.ktor.http.Cookie
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PersistentSessionTest {
    private class MemoryStore : SecureSessionStore {
        var value: String? = null
        override fun read() = value
        override fun write(value: String) { this.value = value }
        override fun clear() { value = null }
    }

    @Test
    fun restoresAcrossInstancesAndExpiresAtFourteenDaysWithoutUse() {
        val store = MemoryStore()
        var now = 0L
        PersistentSession(store) { now }.save(Cookie("sp_refresh", "refresh", maxAge = 30 * 86400))
        val restored = PersistentSession(store) { now }
        now = 14L * 86400 * 1000 - 1
        assertEquals("refresh", restored.refreshToken())
        now++
        assertNull(restored.refreshToken())
        assertNull(store.value)
    }

    @Test
    fun foregroundUseExtendsInactivityButNotServerExpiry() {
        val store = MemoryStore()
        var now = 0L
        val session = PersistentSession(store) { now }
        session.save(Cookie("sp_refresh", "refresh", maxAge = 20 * 86400))
        now = 10L * 86400 * 1000
        session.markActive()
        now = 19L * 86400 * 1000
        assertEquals("refresh", session.refreshToken())
        now = 20L * 86400 * 1000
        assertNull(session.refreshToken())
    }

    @Test
    fun rotationReplacesTokenAndDeletionClearsIt() {
        val store = MemoryStore()
        val session = PersistentSession(store) { 0L }
        session.save(Cookie("sp_refresh", "old", maxAge = 86400))
        session.save(Cookie("sp_refresh", "new", maxAge = 86400))
        assertEquals("new", session.refreshToken())
        session.save(Cookie("sp_refresh", "", maxAge = 0))
        assertNull(session.refreshToken())
    }
}