package com.flamyoad.tsukiviewer.security

import android.content.Context
import android.os.SystemClock
import android.util.AtomicFile
import java.io.File
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** The preference is device-local. The authenticated session lives only in memory. */
internal class AppLock(context: Context) {
    // noBackupFilesDir also excludes this preference from device-to-device transfers.
    private val setting = File(context.noBackupFilesDir, "app_lock_enabled")
    private val enabledState = MutableStateFlow(setting.exists())
    val enabled = enabledState.asStateFlow()

    private val timeoutFile = AtomicFile(File(context.noBackupFilesDir, "app_lock_timeout"))
    private val timeoutState = MutableStateFlow(readTimeout())
    val timeout = timeoutState.asStateFlow()

    private val session = AppLockSession()
    private var startedActivities = 0

    val isLocked: Boolean
        get() = enabled.value && session.isLocked(SystemClock.elapsedRealtime(), timeout.value.milliseconds)

    fun activityStarted() {
        startedActivities++
    }

    fun activityStopped(changingConfigurations: Boolean) {
        startedActivities--
        if (startedActivities == 0 && !changingConfigurations) {
            session.backgrounded(SystemClock.elapsedRealtime())
        }
    }

    fun activityResumed() {
        session.foregrounded(SystemClock.elapsedRealtime(), timeout.value.milliseconds)
    }

    fun setTimeout(value: AppLockTimeout): Boolean {
        if (timeout.value == value) return true
        val stream = try {
            timeoutFile.startWrite()
        } catch (_: IOException) {
            return false
        }
        try {
            stream.write(value.minutes.toString().encodeToByteArray())
            timeoutFile.finishWrite(stream)
        } catch (_: IOException) {
            timeoutFile.failWrite(stream)
            return false
        }
        timeoutState.value = value
        return true
    }

    private fun readTimeout(): AppLockTimeout {
        val minutes = try {
            timeoutFile.readFully().decodeToString().toIntOrNull()
        } catch (_: IOException) {
            null
        }
        return AppLockTimeout.entries.find { it.minutes == minutes } ?: AppLockTimeout.ONE_MINUTE
    }

    fun authenticated() {
        session.unlock()
    }

    fun setEnabledAfterAuthentication(value: Boolean): Boolean {
        val saved = try {
            if (value) {
                setting.createNewFile() || setting.exists()
            } else {
                setting.delete() || !setting.exists()
            }
        } catch (_: IOException) {
            false
        }
        if (saved) {
            authenticated()
            enabledState.value = value
        }
        return saved
    }
}
