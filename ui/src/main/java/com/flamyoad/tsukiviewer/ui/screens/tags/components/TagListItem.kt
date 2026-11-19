package com.flamyoad.tsukiviewer.ui.screens.tags.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flamyoad.tsukiviewer.ui.screens.tags.TagType
import com.flamyoad.tsukiviewer.ui.screens.tags.TagUiModel
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

@Composable
fun TagListItem(
    tag: TagUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tag.name,
                color = Color(0xFF2B2B2B),
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = Color(0xFF959595))
            Text(
                text = tag.count.toString(),
                fontSize = 14.sp,
                color = Color(0xFF959595),
                modifier = Modifier.widthIn(min = 30.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TagListItemPreview() {
    TsukiViewerTheme {
        TagListItem(
            tag = TagUiModel(
                id = 1,
                name = "Sample Tag Name",
                type = TagType.ARTISTS,
                count = 42
            ),
            onClick = {},
            modifier = Modifier.padding(8.dp)
        )
    }
}
