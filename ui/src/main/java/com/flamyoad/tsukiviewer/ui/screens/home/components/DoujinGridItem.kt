package com.flamyoad.tsukiviewer.ui.screens.home.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flamyoad.tsukiviewer.ui.components.selectionZoom
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.flamyoad.tsukiviewer.ui.screens.home.DoujinUiModel
import com.flamyoad.tsukiviewer.ui.screens.home.ViewMode
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

/**
 * Composable that displays a single Doujin item in a grid.
 */
@Composable
fun DoujinGridItem(
    doujin: DoujinUiModel,
    viewMode: ViewMode,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    selectionActive: Boolean = doujin.isSelected
) {
    val aspectRatio = when (viewMode) {
        ViewMode.SCALED -> 2f / 3f
        ViewMode.NORMAL_GRID -> 1f
        ViewMode.MINI_GRID -> 1f
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .selectionZoom(selectionActive, onClick, onLongClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
                .aspectRatio(aspectRatio)
        ) {
            // Cover Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(doujin.coverUri)
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
            
            // Title and page count treatment used by the View cards.
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0xB3000000))
                    .padding(start = 30.dp, end = 4.dp)
            ) {
                Text(
                    text = doujin.title,
                    fontSize = 14.sp,
                    lineHeight = 17.sp,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = doujin.numberOfItems.toString(),
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                    color = Color(0xFFCFCFCF),
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
        ViewMode.SCALED -> 2f / 3f
        ViewMode.NORMAL_GRID -> 1f
        ViewMode.MINI_GRID -> 1f
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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
