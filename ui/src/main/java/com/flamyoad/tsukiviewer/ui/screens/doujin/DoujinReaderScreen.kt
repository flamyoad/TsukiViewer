package com.flamyoad.tsukiviewer.ui.screens.doujin

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.flamyoad.tsukiviewer.core.model.ReaderSpreadPreferences
import com.flamyoad.tsukiviewer.core.model.ReaderSpreads
import com.flamyoad.tsukiviewer.core.prefs.MyAppPreference
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DoujinReaderScreen(
    doujinPath: String,
    initialPage: Int = 0,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReaderViewModel = viewModel(
        factory = ReaderViewModel.Factory(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val sharedPreferences = remember(context) { MyAppPreference.getInstance(context).prefs }
    var spreadPreferences by remember(sharedPreferences) {
        mutableStateOf(ReaderSpreadPreferences.read(sharedPreferences))
    }
    val window = LocalConfiguration.current
    val twoPageMode = uiState.readerMode == ReaderMode.TWO_PAGE
    val spreadLayout = ReaderSpreads(
        uiState.totalPages,
        spreadPreferences.useSpreads(window.screenWidthDp, window.screenHeightDp, twoPageMode),
        spreadPreferences.separateCover,
        spreadPreferences.rightToLeft
    )
    val visiblePages = if (
        (twoPageMode || uiState.readerMode == ReaderMode.HORIZONTAL_SWIPE) &&
            uiState.currentPage in 0 until uiState.totalPages
    ) spreadLayout.pagesForItem(spreadLayout.itemForPage(uiState.currentPage))
    else uiState.currentPage..uiState.currentPage
    val pageLabel = if (visiblePages.first == visiblePages.last) "${visiblePages.first + 1}"
        else "${visiblePages.first + 1}–${visiblePages.last + 1}"

    // Load images when the screen is first composed
    LaunchedEffect(doujinPath) {
        viewModel.loadImages(doujinPath, initialPage)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (uiState.isLoading) {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        } else if (uiState.directoryNotFound) {
            // Error state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Directory not found",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.snackbarMessage ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            // Reader content
            ReaderContent(
                uiState = uiState,
                spreadLayout = spreadLayout,
                onPageChange = {
                    viewModel.setCurrentPage(it)
                    if (twoPageMode) viewModel.hideOverlays()
                },
                onTap = viewModel::toggleControls
            )
        }

        // Top bar overlay
        AnimatedVisibility(
            visible = (if (twoPageMode) uiState.showModeSelector else uiState.showControls) &&
                !uiState.isLoading && !uiState.directoryNotFound,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            ReaderTopBar(
                title = uiState.directoryName,
                currentPage = pageLabel,
                totalPages = uiState.totalPages,
                onNavigateBack = onNavigateBack
            )
        }

        // Bottom page indicator bar
        AnimatedVisibility(
            visible = uiState.showControls && !uiState.isLoading && !uiState.directoryNotFound,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ReaderBottomBar(
                currentPage = pageLabel,
                totalPages = uiState.totalPages,
                onPageIndicatorClick = { viewModel.toggleThumbnailBar() },
                onModeClick = if (twoPageMode) null else viewModel::toggleModeSelector
            )
        }

        if (twoPageMode && !uiState.isLoading && !uiState.directoryNotFound) {
            IconButton(
                onClick = viewModel::toggleModeSelector,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
            ) {
                Icon(Icons.Default.ViewList, contentDescription = "Reader mode", tint = Color.White)
            }
        }

        // Mode selector (right side)
        AnimatedVisibility(
            visible = uiState.showModeSelector,
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            ReaderModeSelector(
                currentMode = uiState.readerMode,
                onModeSelect = viewModel::setReaderMode,
                spreadPreferences = spreadPreferences,
                onSpreadPreferencesChange = {
                    spreadPreferences = it
                    it.save(sharedPreferences)
                }
            )
        }

        // Thumbnail bar (bottom)
        AnimatedVisibility(
            visible = uiState.showThumbnailBar,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        ) {
            ThumbnailBar(
                images = uiState.imageList,
                currentPage = uiState.currentPage,
                onThumbnailClick = { page ->
                    viewModel.setCurrentPage(page)
                }
            )
        }

        // Snackbar
        uiState.snackbarMessage?.let { message ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(message)
            }
            LaunchedEffect(message) {
                kotlinx.coroutines.delay(3000)
                viewModel.clearSnackbarMessage()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReaderContent(
    uiState: ReaderUiState,
    spreadLayout: ReaderSpreads,
    onPageChange: (Int) -> Unit,
    onTap: () -> Unit
) {
    when (uiState.readerMode) {
        ReaderMode.HORIZONTAL_SWIPE, ReaderMode.TWO_PAGE -> key(spreadLayout) {
            HorizontalSwipeReader(
                spreads = spreadLayout,
                images = uiState.imageList,
                currentPage = uiState.currentPage,
                onPageChange = onPageChange,
                onTap = onTap
            )
        }
        ReaderMode.VERTICAL_SWIPE -> {
            val pagerState = rememberPagerState(
                initialPage = uiState.currentPage,
                pageCount = { uiState.imageList.size }
            )
            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }.collect(onPageChange)
            }
            LaunchedEffect(uiState.currentPage) {
                if (pagerState.currentPage != uiState.currentPage) {
                    pagerState.animateScrollToPage(uiState.currentPage)
                }
            }
            VerticalSwipeReader(pagerState, uiState.imageList, onTap)
        }
        ReaderMode.VERTICAL_STRIP -> VerticalStripReader(
            images = uiState.imageList,
            initialPage = uiState.currentPage,
            onPageChange = onPageChange,
            onTap = onTap
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HorizontalSwipeReader(
    spreads: ReaderSpreads,
    images: List<Uri>,
    currentPage: Int,
    onPageChange: (Int) -> Unit,
    onTap: () -> Unit
) {
    val desiredItem = spreads.itemForPage(currentPage)
    val pagerState = rememberPagerState(initialPage = desiredItem, pageCount = { spreads.itemCount })
    LaunchedEffect(pagerState, currentPage) {
        if (pagerState.currentPage != desiredItem) pagerState.scrollToPage(desiredItem)
        snapshotFlow { pagerState.settledPage }.collect { item ->
            val pages = spreads.pagesForItem(item)
            if (currentPage !in pages) onPageChange(pages.first)
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onTap() })
                }
        ) { item ->
            val pages = spreads.pagesForItem(item)
            val visualOrder = if (spreads.rightToLeft) pages.reversed() else pages
            Row(Modifier.fillMaxSize()) {
                for (page in visualOrder) {
                    ReaderImage(
                        uri = images[page],
                        contentDescription = "Page ${page + 1}",
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VerticalSwipeReader(
    pagerState: PagerState,
    images: List<Uri>,
    onTap: () -> Unit
) {
    VerticalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onTap() })
            }
    ) { page ->
        ReaderImage(
            uri = images[page],
            contentDescription = "Page ${page + 1}",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun VerticalStripReader(
    images: List<Uri>,
    initialPage: Int,
    onPageChange: (Int) -> Unit,
    onTap: () -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialPage)

    // Track visible item for page indicator
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                onPageChange(index)
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onTap() })
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(images) { index, uri ->
            ReaderImage(
                uri = uri,
                contentDescription = "Page ${index + 1}",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@Composable
private fun ReaderImage(
    uri: Uri,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(uri)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReaderTopBar(
    title: String,
    currentPage: String,
    totalPages: Int,
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = "Page $currentPage / $totalPages",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black.copy(alpha = 0.8f),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        ),
        windowInsets = WindowInsets(0, 0, 0, 0)
    )
}

@Composable
private fun ReaderBottomBar(
    currentPage: String,
    totalPages: Int,
    onPageIndicatorClick: () -> Unit,
    onModeClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.8f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Page indicator (clickable to show thumbnails)
        Text(
            text = "Page: $currentPage / $totalPages",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .clickable { onPageIndicatorClick() }
                .padding(8.dp)
        )

        if (onModeClick != null) {
            IconButton(onClick = onModeClick) {
                Icon(
                    imageVector = Icons.Default.ViewList,
                    contentDescription = "Reader mode",
                    tint = Color.White
                )
            }
        } else {
            Spacer(Modifier.size(48.dp))
        }
    }
}

@Composable
private fun ReaderModeSelector(
    currentMode: ReaderMode,
    onModeSelect: (ReaderMode) -> Unit,
    spreadPreferences: ReaderSpreadPreferences,
    onSpreadPreferencesChange: (ReaderSpreadPreferences) -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.9f))
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReaderModeButton(
            icon = Icons.Default.SwapHoriz,
            label = "Horizontal",
            isSelected = currentMode == ReaderMode.HORIZONTAL_SWIPE,
            onClick = { onModeSelect(ReaderMode.HORIZONTAL_SWIPE) }
        )
        ReaderModeButton(
            icon = Icons.Default.SwapVert,
            label = "Vertical",
            isSelected = currentMode == ReaderMode.VERTICAL_SWIPE,
            onClick = { onModeSelect(ReaderMode.VERTICAL_SWIPE) }
        )
        ReaderModeButton(
            icon = Icons.Default.ViewList,
            label = "Webtoon",
            isSelected = currentMode == ReaderMode.VERTICAL_STRIP,
            onClick = { onModeSelect(ReaderMode.VERTICAL_STRIP) }
        )
        ReaderModeButton(
            icon = Icons.Default.ViewColumn,
            label = "Two-page",
            isSelected = currentMode == ReaderMode.TWO_PAGE,
            onClick = { onModeSelect(ReaderMode.TWO_PAGE) }
        )
        if (currentMode == ReaderMode.HORIZONTAL_SWIPE || currentMode == ReaderMode.TWO_PAGE) {
            if (currentMode == ReaderMode.HORIZONTAL_SWIPE) {
                ReaderOption("Automatic two-page spreads", spreadPreferences.enabled) {
                    onSpreadPreferencesChange(spreadPreferences.copy(enabled = it))
                }
            }
            ReaderOption("Separate cover", spreadPreferences.separateCover) {
                onSpreadPreferencesChange(spreadPreferences.copy(separateCover = it))
            }
            ReaderOption("Read right to left", spreadPreferences.rightToLeft) {
                onSpreadPreferencesChange(spreadPreferences.copy(rightToLeft = it))
            }
        }
    }
}

@Composable
private fun ReaderOption(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color.White, modifier = Modifier.padding(end = 12.dp))
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun ReaderModeButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        Color.White
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}

@Composable
private fun ThumbnailBar(
    images: List<Uri>,
    currentPage: Int,
    onThumbnailClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Scroll to current page when it changes
    LaunchedEffect(currentPage) {
        scope.launch {
            listState.animateScrollToItem(currentPage)
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.9f))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        itemsIndexed(images) { index, uri ->
            ThumbnailItem(
                uri = uri,
                isSelected = index == currentPage,
                pageNumber = index + 1,
                onClick = { onThumbnailClick(index) }
            )
        }
    }
}

@Composable
private fun ThumbnailItem(
    uri: Uri,
    isSelected: Boolean,
    pageNumber: Int,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    Box(
        modifier = Modifier
            .height(80.dp)
            .width(60.dp)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(4.dp)
            )
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .crossfade(true)
                .size(120, 160) // Thumbnail size
                .build(),
            contentDescription = "Page $pageNumber thumbnail",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Page number overlay
        Text(
            text = pageNumber.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DoujinReaderScreenPreview() {
    TsukiViewerTheme {
        DoujinReaderScreen(
            doujinPath = "/storage/emulated/0/Doujins/Sample",
            initialPage = 0,
            onNavigateBack = {}
        )
    }
}
