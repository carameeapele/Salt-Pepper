package com.example.saltpepper.core.session
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
        PersistentSession(store) { now }.save("refresh", 30L * 86400 * 1000)
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
        session.save("refresh", 20L * 86400 * 1000)
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
        session.save("old", 86400L * 1000)
        session.save("new", 86400L * 1000)
        assertEquals("new", session.refreshToken())
        session.save("", 0)
        assertNull(session.refreshToken())
    }
}