package com.flamyoad.tsukiviewer.ui.screens.settings.includedfolders.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flamyoad.tsukiviewer.ui.screens.settings.includedfolders.IncludedFolderUiModel
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

/**
 * Composable for displaying an included folder item.
 */
@Composable
fun IncludedFolderItem(
    folder: IncludedFolderUiModel,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Folder icon - show warning if folder doesn't exist
            Icon(
                imageVector = if (folder.exists) Icons.Filled.Folder else Icons.Filled.Warning,
                contentDescription = if (folder.exists) "Folder" else "Folder not found",
                tint = if (folder.exists) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
            
            // Folder info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = folder.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (folder.exists) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
                Text(
                    text = folder.path,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!folder.exists) {
                    Text(
                        text = "Folder not found",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            // Delete button
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Remove folder",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
        
        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
private fun IncludedFolderItemPreview() {
    TsukiViewerTheme {
        Column {
            IncludedFolderItem(
                folder = IncludedFolderUiModel(
                    id = 1,
                    path = "/storage/emulated/0/Download/Manga",
                    displayName = "Manga",
                    exists = true
                ),
                onDeleteClick = {}
            )
            IncludedFolderItem(
                folder = IncludedFolderUiModel(
                    id = 2,
                    path = "/storage/emulated/0/Missing/Folder",
                    displayName = "Folder",
                    exists = false
                ),
                onDeleteClick = {}
            )
        }
    }
}
