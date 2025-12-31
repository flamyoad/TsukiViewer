package com.flamyoad.tsukiviewer.ui.screens.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.flamyoad.tsukiviewer.ui.screens.home.DoujinUiModel
import com.flamyoad.tsukiviewer.ui.screens.home.ViewMode
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

/**
 * Composable that displays a single Doujin item in a grid.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DoujinGridItem(
    doujin: DoujinUiModel,
    viewMode: ViewMode,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val aspectRatio = when (viewMode) {
        ViewMode.SCALED -> 0.67f // 2:3 ratio for scaled view
        ViewMode.NORMAL_GRID -> 0.75f
        ViewMode.MINI_GRID -> 0.75f
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
        ) {
            // Cover Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(doujin.coverUri)
                    .crossfade(true)
                    .build(),
                contentDescription = doujin.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Selection indicator
            if (doujin.isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                )
                
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
            
            // Bottom gradient overlay with title and page count
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = doujin.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "${doujin.numberOfItems} pages",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * Loading placeholder for a doujin grid item.
 */
@Composable
fun DoujinGridItemPlaceholder(
    viewMode: ViewMode,
    modifier: Modifier = Modifier
) {
    val aspectRatio = when (viewMode) {
        ViewMode.SCALED -> 0.67f
        ViewMode.NORMAL_GRID -> 0.75f
        ViewMode.MINI_GRID -> 0.75f
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DoujinGridItemPreview() {
    TsukiViewerTheme {
        // Preview would need actual data
    }
}
