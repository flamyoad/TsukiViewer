package com.flamyoad.tsukiviewer.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppLockSessionTest {
    @Test
    fun newSessionStartsLocked() {
        assertTrue(AppLockSession().isLocked(now = 0, timeoutMillis = null))
    }

    @Test
    fun shortBackgroundStayPreservesUnlock() {
        val session = AppLockSession()
        session.unlock()
        session.backgrounded(now = 1_000)
        session.foregrounded(now = 60_999, timeoutMillis = 60_000)

        assertFalse(session.isLocked(now = 120_000, timeoutMillis = 60_000))
    }

    @Test
    fun sixtySecondsInBackgroundRequiresAuthenticationAgain() {
        val session = AppLockSession()
        session.unlock()
        session.backgrounded(now = 1_000)

        assertFalse(session.isLocked(now = 60_999, timeoutMillis = 60_000))
        assertTrue(session.isLocked(now = 61_000, timeoutMillis = 60_000))
        session.foregrounded(now = 61_000, timeoutMillis = 60_000)
        assertTrue(session.isLocked(now = 61_001, timeoutMillis = 60_000))
        session.unlock()
        assertFalse(session.isLocked(now = 61_001, timeoutMillis = 60_000))
    }

    @Test
    fun configuredTimeoutAppliesOnReturn() {
        val session = AppLockSession()
        session.unlock()
        session.backgrounded(now = 500)

        assertFalse(session.isLocked(now = 300_499, timeoutMillis = 300_000))
        assertTrue(session.isLocked(now = 300_500, timeoutMillis = 300_000))
    }

    @Test
    fun immediateTimeoutRequiresABackgroundTransition() {
        val session = AppLockSession()
        session.unlock()
        assertFalse(session.isLocked(now = 100, timeoutMillis = 0))

        session.backgrounded(now = 100)
        assertTrue(session.isLocked(now = 100, timeoutMillis = 0))
    }

    @Test
    fun coldStartOnlyStaysUnlockedAcrossLongBackgroundStays() {
        val session = AppLockSession()
        session.unlock()
        session.backgrounded(now = 1_000)

        session.foregrounded(now = 86_400_000, timeoutMillis = null)
        assertFalse(session.isLocked(now = 86_400_000, timeoutMillis = null))
        assertTrue(AppLockSession().isLocked(now = 86_400_000, timeoutMillis = null))
    }
}
