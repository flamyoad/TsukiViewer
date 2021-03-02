package com.flamyoad.tsukiviewer.utils

// Contains intents of the activities in back stack (DoujinDetailsActivity.kt and SearchResultActivity.kt)
sealed class ActivityHistory {

    data class DoujinDetailsActivity(val dirPath: String = "", val doujinName: String = "") :
        ActivityHistory()

    data class SearchResultActivity(
        val tags: String = "",
        val title: String = "",
        val includeAllTags: Boolean = false
    ) : ActivityHistory()
}