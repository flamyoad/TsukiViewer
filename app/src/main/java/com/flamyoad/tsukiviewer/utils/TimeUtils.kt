package com.flamyoad.tsukiviewer.utils

import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

object TimeUtils {
    fun getReadableDate(epochMilli: Long): String {
        val dateTime = Instant.ofEpochMilli(epochMilli)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

        val formatter = DateTimeFormatter.ofPattern("d MMM, yyyy h:mm:ss a")
        return dateTime.format(formatter)
    }
}