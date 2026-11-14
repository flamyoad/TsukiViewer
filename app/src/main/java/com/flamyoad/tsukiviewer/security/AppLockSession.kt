package com.flamyoad.tsukiviewer.security

internal class AppLockSession {
    private var unlocked = false
    private var backgroundedAt: Long? = null

    fun isLocked(now: Long, timeoutMillis: Long?): Boolean {
        if (!unlocked) return true
        val since = backgroundedAt ?: return false
        return timeoutMillis != null && now - since >= timeoutMillis
    }

    fun foregrounded(now: Long, timeoutMillis: Long?) {
        unlocked = !isLocked(now, timeoutMillis)
        backgroundedAt = null
    }

    fun backgrounded(now: Long) {
        backgroundedAt = now
    }

    fun unlock() {
        unlocked = true
        backgroundedAt = null
    }
}
