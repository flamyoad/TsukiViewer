package com.flamyoad.tsukiviewer.ui.screens.home.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.flamyoad.tsukiviewer.ui.screens.home.ViewMode

/**
 * Dropdown menu for selecting view mode (grid size).
 */
@Composable
fun ViewModeMenu(
    expanded: Boolean,
    currentViewMode: ViewMode,
    onViewModeSelected: (ViewMode) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        ViewModeMenuItem(
            viewMode = ViewMode.NORMAL_GRID,
            icon = Icons.Default.GridOn,
            label = "Normal Grid",
            isSelected = currentViewMode == ViewMode.NORMAL_GRID,
            onClick = {
                onViewModeSelected(ViewMode.NORMAL_GRID)
                onDismiss()
            }
        )
        
        ViewModeMenuItem(
            viewMode = ViewMode.SCALED,
            icon = Icons.Default.ViewModule,
            label = "Scaled",
            isSelected = currentViewMode == ViewMode.SCALED,
            onClick = {
                onViewModeSelected(ViewMode.SCALED)
                onDismiss()
            }
        )
        
        ViewModeMenuItem(
            viewMode = ViewMode.MINI_GRID,
            icon = Icons.Default.Apps,
            label = "Mini Grid",
            isSelected = currentViewMode == ViewMode.MINI_GRID,
            onClick = {
                onViewModeSelected(ViewMode.MINI_GRID)
                onDismiss()
            }
        )
    }
}

@Composable
private fun ViewModeMenuItem(
    viewMode: ViewMode,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(label) },
        onClick = onClick,
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = label
            )
        }
    )
}
