package com.flamyoad.tsukiviewer.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
import com.flamyoad.tsukiviewer.ui.theme.ColorPrimary
import com.flamyoad.tsukiviewer.ui.theme.SubLightTextColor
import com.flamyoad.tsukiviewer.ui.theme.TextWhite
import com.flamyoad.tsukiviewer.ui.theme.WineRed

/**
 * Data class representing a bottom navigation item.
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * List of bottom navigation items for the main screens.
 */
val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Home.route,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = Screen.Collections.route,
        label = "Collections",
        selectedIcon = Icons.Filled.Folder,
        unselectedIcon = Icons.Outlined.Folder
    ),
    BottomNavItem(
        route = Screen.Tags.route,
        label = "Tags",
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Outlined.List
    ),
    BottomNavItem(
        route = Screen.Bookmarks.route,
        label = "Bookmarks",
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.Bookmark
    ),
    BottomNavItem(
        route = Screen.Settings.route,
        label = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
)

/**
 * Main navigation host that contains all destinations.
 * Main screens (Home, Collections, Tags, Bookmarks) are wrapped in MainScaffold with BottomNav.
 * Other screens navigate outside the scaffold.
 */
@Composable
fun TsukiNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Main.route,
    onRestartApp: () -> Unit = {}
) {
    val animationDuration = 300
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(250)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(250)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(250)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(250)) }
    ) {
        // Main screen with bottom navigation
        composable(Screen.Main.route) {
            MainScaffold(
                onDoujinClick = { doujinPath ->
                    navController.navigate(Screen.DoujinDetails.createRoute(doujinPath))
                },
                onSearchClick = {
                    navController.navigate(Screen.Search.route)
                },
                onNavigateToSettings = { },
                onNavigateToIncludedFolders = {
                    navController.navigate(Screen.IncludedFolders.route)
                },
                onCollectionClick = { collectionId ->
                    navController.navigate(Screen.CollectionDetails.createRoute(collectionId))
                },
                onTagClick = { tagId ->
                    navController.navigate(Screen.TagDetails.createRoute(tagId))
                },
                onRestartApp = onRestartApp
            )
        }
        
        composable(Screen.Search.route) {
            Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                SearchScreen(
                    onDoujinClick = { doujinPath ->
                        navController.navigate(Screen.DoujinDetails.createRoute(doujinPath))
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        composable(Screen.Settings.route) {
            Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
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
        }
        
        composable(Screen.IncludedFolders.route) {
            Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                IncludedFoldersScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        composable(
            route = Screen.DoujinDetails.route,
            arguments = listOf(
                navArgument("doujinPath") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedPath = backStackEntry.arguments?.getString("doujinPath") ?: return@composable
            val doujinPath = Screen.DoujinDetails.decodePath(encodedPath)
            Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
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
            Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                DoujinReaderScreen(
                    doujinPath = doujinPath,
                    initialPage = initialPage,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        composable(
            route = Screen.CollectionDetails.route,
            arguments = listOf(
                navArgument("collectionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getLong("collectionId") ?: return@composable
            Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
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
        }
        
        composable(
            route = Screen.TagDetails.route,
            arguments = listOf(
                navArgument("tagId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val tagId = backStackEntry.arguments?.getLong("tagId") ?: return@composable
            Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
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
}

/**
 * Main scaffold with bottom navigation for the main screens.
 * Contains its own NavHost for switching between Home, Collections, Tags, and Bookmarks.
 */
@Composable
fun MainScaffold(
    onDoujinClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToIncludedFolders: () -> Unit,
    onCollectionClick: (Long) -> Unit,
    onTagClick: (Long) -> Unit,
    onRestartApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = ColorPrimary,
                contentColor = TextWhite
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any {
                        it.route == item.route
                    } == true

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = selected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = WineRed,
                            selectedTextColor = TextWhite,
                            indicatorColor = ColorPrimary,
                            unselectedIconColor = SubLightTextColor,
                            unselectedTextColor = SubLightTextColor
                        ),
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onDoujinClick = onDoujinClick,
                    onSearchClick = onSearchClick,
                    onNavigateToSettings = onNavigateToSettings
                )
            }

            composable(Screen.Collections.route) {
                CollectionsScreen(
                    onCollectionClick = onCollectionClick
                )
            }

            composable(Screen.Tags.route) {
                TagsScreen(
                    onTagClick = onTagClick
                )
            }

            composable(Screen.Bookmarks.route) {
                BookmarksScreen(
                    onDoujinClick = onDoujinClick
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { },
                    onNavigateToIncludedFolders = onNavigateToIncludedFolders,
                    onRestartApp = onRestartApp
                )
            }
        }
    }
}
