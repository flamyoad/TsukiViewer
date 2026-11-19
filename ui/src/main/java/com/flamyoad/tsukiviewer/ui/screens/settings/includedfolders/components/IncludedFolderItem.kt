package com.flamyoad.tsukiviewer.ui.screens.settings.includedfolders.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Close
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
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = folder.path,
                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                fontSize = 18.sp,
                lineHeight = 21.sp,
                color = Color(0xFF757575)
            )
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remove folder",
                    tint = Color(0xFFAEAEAE),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        HorizontalDivider(color = Color(0xFFE4E4E4))
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
