package com.flamyoad.tsukiviewer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flamyoad.tsukiviewer.ui.navigation.Screen
import com.flamyoad.tsukiviewer.ui.navigation.TsukiNavHost
import com.flamyoad.tsukiviewer.ui.theme.ColorPrimary
import com.flamyoad.tsukiviewer.ui.theme.SubLightTextColor
import com.flamyoad.tsukiviewer.ui.theme.TextWhite
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme
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
 * List of bottom navigation items.
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
 * Routes where bottom navigation should be hidden.
 */
private val routesWithoutBottomNav = setOf(
    Screen.DoujinDetails.route,
    Screen.DoujinReader.route,
    Screen.CollectionDetails.route,
    Screen.Search.route
)

/**
 * Main entry point composable for the TsukiViewer app.
 * This can be called from the app module's Activity.
 */
@Composable
fun TsukiViewerApp(
    modifier: Modifier = Modifier,
    onRestartApp: () -> Unit = {}
) {
    TsukiViewerTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        
        // Determine if bottom nav should be shown
        val showBottomNav = currentDestination?.route !in routesWithoutBottomNav
        
        Scaffold(
            bottomBar = {
                if (showBottomNav) {
                    NavigationBar(
                        containerColor = ColorPrimary,  // Dark gray like toolbar
                        contentColor = TextWhite        // White content
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
                                    selectedIconColor = WineRed,           // Accent color for selected icon
                                    selectedTextColor = TextWhite,          // White text when selected
                                    indicatorColor = ColorPrimary,          // No visible indicator background
                                    unselectedIconColor = SubLightTextColor, // Light gray for unselected
                                    unselectedTextColor = SubLightTextColor  // Light gray text
                                ),
                                onClick = {
                                    navController.navigate(item.route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            },
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            TsukiNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                onRestartApp = onRestartApp
            )
        }
    }
}
