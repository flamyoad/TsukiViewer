package com.flamyoad.tsukiviewer.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flamyoad.tsukiviewer.ui.screens.home.DoujinSortingMode
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

/**
 * Dialog for selecting sorting mode for doujins.
 */
@Composable
fun SortDoujinDialog(
    currentSortMode: DoujinSortingMode,
    onSortModeSelected: (DoujinSortingMode) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Sort by",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                DoujinSortingMode.entries
                    .filter { it != DoujinSortingMode.NONE }
                    .forEach { sortMode ->
                        SortOptionRow(
                            sortMode = sortMode,
                            isSelected = sortMode == currentSortMode,
                            onClick = {
                                onSortModeSelected(sortMode)
                                onDismiss()
                            }
                        )
                    }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        modifier = modifier
    )
}

@Composable
private fun SortOptionRow(
    sortMode: DoujinSortingMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
        
        Text(
            text = sortMode.displayName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SortDoujinDialogPreview() {
    TsukiViewerTheme {
        SortDoujinDialog(
            currentSortMode = DoujinSortingMode.TITLE_ASC,
            onSortModeSelected = {},
            onDismiss = {}
        )
    }
}
