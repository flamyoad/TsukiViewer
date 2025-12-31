package com.flamyoad.tsukiviewer.ui.screens.tags.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flamyoad.tsukiviewer.ui.screens.tags.TagSortingMode
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortTagDialog(
    currentMode: TagSortingMode,
    onSortModeSelected: (TagSortingMode) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Sort By",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TagSortingMode.entries.forEach { mode ->
                SortOptionItem(
                    mode = mode,
                    isSelected = mode == currentMode,
                    onSelect = {
                        onSortModeSelected(mode)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun SortOptionItem(
    mode: TagSortingMode,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = mode.displayName,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SortTagDialogPreview() {
    TsukiViewerTheme {
        Column {
            SortOptionItem(
                mode = TagSortingMode.NAME_ASCENDING,
                isSelected = true,
                onSelect = {}
            )
            SortOptionItem(
                mode = TagSortingMode.COUNT_DESCENDING,
                isSelected = false,
                onSelect = {}
            )
        }
    }
}
