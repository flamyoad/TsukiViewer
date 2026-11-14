package com.flamyoad.tsukiviewer.security

import androidx.annotation.StringRes
import com.flamyoad.tsukiviewer.R

internal enum class AppLockTimeout(val minutes: Int, @StringRes val label: Int) {
    IMMEDIATELY(0, R.string.app_lock_timeout_immediately),
    ONE_MINUTE(1, R.string.app_lock_timeout_one_minute),
    FIVE_MINUTES(5, R.string.app_lock_timeout_five_minutes),
    FIFTEEN_MINUTES(15, R.string.app_lock_timeout_fifteen_minutes),
    THIRTY_MINUTES(30, R.string.app_lock_timeout_thirty_minutes),
    COLD_START_ONLY(-1, R.string.app_lock_timeout_cold_start);

    val milliseconds: Long?
        get() = if (this == COLD_START_ONLY) null else minutes * 60_000L
}
