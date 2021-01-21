package com.flamyoad.tsukiviewer.utils

// Contains intents of the activities in back stack (DoujinDetailsActivity.kt and SearchResultActivity.kt)
data class ActivityHistory(
    val dirPath: String = "",
    val doujinName: String = "",

    val tags: String = "",
    val title: String = "",
    val includeAllTags: Boolean = false,

    val activityType: ActivityType
)

enum class ActivityType {
    DoujinDetailsActivity,
    SearchResultActivity,
}