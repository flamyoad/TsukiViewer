package com.flamyoad.tsukiviewer.ui.navigation

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Sealed class representing all navigation destinations in the app.
 */
sealed class Screen(val route: String) {
    
    object Home : Screen("home")
    
    object Search : Screen("search")
    
    object Collections : Screen("collections")
    
    object Tags : Screen("tags")
    
    object Bookmarks : Screen("bookmarks")
    
    object Settings : Screen("settings")
    
    object IncludedFolders : Screen("included_folders")
    
    object DoujinDetails : Screen("doujin_details/{doujinPath}") {
        fun createRoute(doujinPath: String): String {
            val encodedPath = URLEncoder.encode(doujinPath, StandardCharsets.UTF_8.toString())
            return "doujin_details/$encodedPath"
        }
        
        fun decodePath(encodedPath: String): String {
            return URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.toString())
        }
    }
    
    object DoujinReader : Screen("doujin_reader/{doujinPath}?initialPage={initialPage}") {
        fun createRoute(doujinPath: String, initialPage: Int = 0): String {
            val encodedPath = URLEncoder.encode(doujinPath, StandardCharsets.UTF_8.toString())
            return "doujin_reader/$encodedPath?initialPage=$initialPage"
        }
        
        fun decodePath(encodedPath: String): String {
            return URLDecoder.decode(encodedPath, StandardCharsets.UTF_8.toString())
        }
    }
    
    object CollectionDetails : Screen("collection_details/{collectionId}") {
        fun createRoute(collectionId: Long): String = "collection_details/$collectionId"
    }
    
    object TagDetails : Screen("tag_details/{tagId}") {
        fun createRoute(tagId: Long): String = "tag_details/$tagId"
    }
}
