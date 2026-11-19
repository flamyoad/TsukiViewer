package com.flamyoad.tsukiviewer.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
sealed interface Screen : NavKey {
    @Serializable
    data object Home : Screen

    @Serializable
    data object Search : Screen

    @Serializable
    data class SearchResults(val title: String, val tags: String, val requireAllTags: Boolean) : Screen

    @Serializable
    data object Collections : Screen

    @Serializable
    data object Tags : Screen

    @Serializable
    data object Bookmarks : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object IncludedFolders : Screen

    @Serializable
    data class RecentTabs(val activePath: String? = null) : Screen

    @Serializable
    data class MetadataEditor(val paths: List<String>) : Screen

    @Serializable
    data class FetchStatus(val paths: List<String>) : Screen

    @Serializable
    data class DoujinDetails(
        val path: String,
        // Related galleries can lead back to an earlier gallery in this stack.
        val entryId: String = UUID.randomUUID().toString()
    ) : Screen

    @Serializable
    data class DoujinReader(
        val path: String,
        val initialPage: Int = 0,
        val galleryEntry: String? = null
    ) : Screen

    @Serializable
    data class CollectionDetails(val collectionId: Long) : Screen

    @Serializable
    data class CollectionEditor(val collectionId: Long?) : Screen

    @Serializable
    data class TagDetails(val tagId: Long) : Screen
}
