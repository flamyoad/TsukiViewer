package com.flamyoad.tsukiviewer.ui.screens.doujin.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flamyoad.tsukiviewer.ui.screens.doujin.TagUiModel
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

/**
 * A chip component for displaying a tag.
 * The chip has two parts - left side shows tag name, right side shows count.
 */
@Composable
fun DoujinTagChip(
    tag: TagUiModel,
    onClick: (TagUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val tagColor = getTagColor(tag.type)
    
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick(tag) },
        shape = RoundedCornerShape(4.dp),
        tonalElevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tag name (left part)
            Box(
                modifier = Modifier
                    .background(tagColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = tag.name,
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Tag count (right part)
            if (tag.count > 1) {
                Box(
                    modifier = Modifier
                        .background(tagColor.copy(alpha = 0.7f))
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = tag.count.toString(),
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

/**
 * Get color based on tag type.
 */
@Composable
private fun getTagColor(type: String): Color {
    return when (type.lowercase()) {
        "parody" -> Color(0xFFAB47BC)      // Purple
        "character" -> Color(0xFF26A69A)   // Teal
        "tag" -> Color(0xFF7E57C2)         // Deep Purple
        "artist" -> Color(0xFFEF5350)      // Red
        "group" -> Color(0xFF42A5F5)       // Blue
        "language" -> Color(0xFF66BB6A)    // Green
        "category" -> Color(0xFFFF7043)    // Deep Orange
        else -> MaterialTheme.colorScheme.primary
    }
}

@Preview(showBackground = true)
@Composable
private fun DoujinTagChipPreview() {
    TsukiViewerTheme {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            DoujinTagChip(
                tag = TagUiModel(
                    id = 1,
                    name = "azur lane",
                    type = "parody",
                    count = 5234
                ),
                onClick = {}
            )
        }
    }
}
