package com.flamyoad.tsukiviewer.ui.screens.doujin.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flamyoad.tsukiviewer.ui.screens.doujin.TagUiModel
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

/**
 * A group of tags with a label.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagGroup(
    label: String,
    tags: List<TagUiModel>,
    onTagClick: (TagUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tags.isEmpty()) return
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tags.forEach { tag ->
                DoujinTagChip(
                    tag = tag,
                    onClick = onTagClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TagGroupPreview() {
    TsukiViewerTheme {
        TagGroup(
            label = "Parodies",
            tags = listOf(
                TagUiModel(1, "azur lane", "parody", count = 5234),
                TagUiModel(2, "kantai collection", "parody", count = 3422),
                TagUiModel(3, "fate grand order", "parody", count = 8923)
            ),
            onTagClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
