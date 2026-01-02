package com.flamyoad.tsukiviewer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flamyoad.tsukiviewer.ui.screens.bookmarks.BookmarksScreen
import com.flamyoad.tsukiviewer.ui.screens.collections.CollectionDetailsScreen
import com.flamyoad.tsukiviewer.ui.screens.collections.CollectionsScreen
import com.flamyoad.tsukiviewer.ui.screens.doujin.DoujinDetailsScreen
import com.flamyoad.tsukiviewer.ui.screens.doujin.DoujinReaderScreen
import com.flamyoad.tsukiviewer.ui.screens.home.HomeScreen
import com.flamyoad.tsukiviewer.ui.screens.search.SearchScreen
import com.flamyoad.tsukiviewer.ui.screens.settings.SettingsScreen
import com.flamyoad.tsukiviewer.ui.screens.settings.includedfolders.IncludedFoldersScreen
import com.flamyoad.tsukiviewer.ui.screens.tags.TagDetailsScreen
import com.flamyoad.tsukiviewer.ui.screens.tags.TagsScreen

@Composable
fun TsukiNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route,
    onRestartApp: () -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onDoujinClick = { doujinPath ->
                    navController.navigate(Screen.DoujinDetails.createRoute(doujinPath))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        composable(Screen.Search.route) {
            SearchScreen(
                onDoujinClick = { doujinPath ->
                    navController.navigate(Screen.DoujinDetails.createRoute(doujinPath))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Collections.route) {
            CollectionsScreen(
                onCollectionClick = { collectionId ->
                    navController.navigate(Screen.CollectionDetails.createRoute(collectionId))
                }
            )
        }
        
        composable(Screen.Tags.route) {
            TagsScreen(
                onTagClick = { tagId ->
                    navController.navigate(Screen.TagDetails.createRoute(tagId))
                }
            )
        }
        
        composable(Screen.Bookmarks.route) {
            BookmarksScreen(
                onDoujinClick = { doujinPath ->
                    navController.navigate(Screen.DoujinDetails.createRoute(doujinPath))
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToIncludedFolders = {
                    navController.navigate(Screen.IncludedFolders.route)
                },
                onRestartApp = onRestartApp
            )
        }
        
        composable(Screen.IncludedFolders.route) {
            IncludedFoldersScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.DoujinDetails.route,
            arguments = listOf(
                navArgument("doujinPath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedPath = backStackEntry.arguments?.getString("doujinPath") ?: return@composable
            val doujinPath = Screen.DoujinDetails.decodePath(encodedPath)
            DoujinDetailsScreen(
                doujinPath = doujinPath,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onReadClick = {
                    navController.navigate(Screen.DoujinReader.createRoute(doujinPath))
                },
                onImageClick = { index ->
                    navController.navigate(Screen.DoujinReader.createRoute(doujinPath, index))
                }
            )
        }
        
        composable(
            route = Screen.DoujinReader.route,
            arguments = listOf(
                navArgument("doujinPath") { type = NavType.StringType },
                navArgument("initialPage") { 
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val encodedPath = backStackEntry.arguments?.getString("doujinPath") ?: return@composable
            val doujinPath = Screen.DoujinReader.decodePath(encodedPath)
            val initialPage = backStackEntry.arguments?.getInt("initialPage") ?: 0
            DoujinReaderScreen(
                doujinPath = doujinPath,
                initialPage = initialPage,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.CollectionDetails.route,
            arguments = listOf(
                navArgument("collectionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getLong("collectionId") ?: return@composable
            CollectionDetailsScreen(
                collectionId = collectionId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onDoujinClick = { doujinPath ->
                    navController.navigate(Screen.DoujinDetails.createRoute(doujinPath))
                }
            )
        }
        
        composable(
            route = Screen.TagDetails.route,
            arguments = listOf(
                navArgument("tagId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val tagId = backStackEntry.arguments?.getLong("tagId") ?: return@composable
            TagDetailsScreen(
                tagId = tagId,
                onDoujinClick = { doujinPath ->
                    navController.navigate(Screen.DoujinDetails.createRoute(doujinPath))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
