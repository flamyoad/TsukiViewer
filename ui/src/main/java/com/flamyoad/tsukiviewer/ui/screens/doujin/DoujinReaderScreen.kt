package com.flamyoad.tsukiviewer.ui.screens.doujin

import android.net.Uri
import android.os.Build
import android.view.KeyEvent
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Tab
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.staticCompositionLocalOf
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.flamyoad.tsukiviewer.core.model.ReaderSpreadPreferences
import com.flamyoad.tsukiviewer.core.model.ReaderSpreads
import com.flamyoad.tsukiviewer.core.prefs.MyAppPreference
import com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys
import com.flamyoad.tsukiviewer.ui.navigation.readerPageTransition
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

private data class VolumeScroll(val sequence: Int, val pixels: Float)

private val ReaderChrome = Color(0xFF5A5757)

private val LocalGifAutoplay = staticCompositionLocalOf { true }
private val SlideInEasing = AccelerateInterpolator()
private val SlideOutEasing = DecelerateInterpolator()

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DoujinReaderScreen(
    doujinPath: String,
    initialPage: Int = 0,
    onNavigateBack: () -> Unit,
    onRecentTabs: () -> Unit = {},
    onCurrentPage: (Int) -> Unit = {},
    setReaderVolumeHandler: (((KeyEvent) -> Boolean)?) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ReaderViewModel = viewModel(
        factory = ReaderViewModel.Factory(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val preferences = remember(context) { MyAppPreference.getInstance(context) }
    val sharedPreferences = preferences.prefs
    val spreadPreferences = remember(sharedPreferences) { ReaderSpreadPreferences.read(sharedPreferences) }
    var sidebarOpen by rememberSaveable { mutableStateOf(false) }
    var lastPage by rememberSaveable(doujinPath) { mutableStateOf(initialPage) }
    val sidebarWidth by animateDpAsState(
        targetValue = if (sidebarOpen) 76.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 300,
            easing = if (sidebarOpen) SlideInEasing::getInterpolation else SlideOutEasing::getInterpolation
        ),
        label = "Page sidebar"
    )
    val tabsFlow = remember(context) { AppDatabase.getInstance(context).recentTabDao().observeAll() }
    val recentTabs by tabsFlow.collectAsStateWithLifecycle(initialValue = emptyList())
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

    var scrollSequence by remember { mutableIntStateOf(0) }
    var volumeScroll by remember { mutableStateOf<VolumeScroll?>(null) }
    val onVolumeKey by rememberUpdatedState<(KeyEvent) -> Boolean> { event ->
        if (!preferences.shouldScrollWithVolumeButton()) false
        else {
            if (event.action == KeyEvent.ACTION_DOWN && uiState.totalPages > 0) {
                val action = if (event.keyCode == KeyEvent.KEYCODE_VOLUME_UP) preferences.getVolumeUpAction()
                    else preferences.getVolumeDownAction()
                val delta = when (action) {
                    "GoToNextPage" -> 1
                    "GoToPrevPage" -> -1
                    else -> 0
                }
                if (delta != 0) {
                    if (uiState.readerMode == ReaderMode.VERTICAL_STRIP &&
                        preferences.getVolumeButtonScrollMode() == "FixedDistance"
                    ) {
                        val distance = preferences.getVolumeButtonScrollDistance().takeIf { it > 0 } ?: 250
                        volumeScroll = VolumeScroll(++scrollSequence, delta * distance.toFloat())
                    } else {
                        val page = if (delta > 0) visiblePages.last + 1 else visiblePages.first - 1
                        viewModel.jumpToPage(page.coerceIn(0, uiState.totalPages - 1))
                    }
                }
            }
            true
        }
    }
    LifecycleResumeEffect(Unit) {
        setReaderVolumeHandler { event -> onVolumeKey(event) }
        onPauseOrDispose { setReaderVolumeHandler(null) }
    }

    // Load images when the screen is first composed
    LaunchedEffect(doujinPath) {
        viewModel.loadImages(doujinPath, lastPage)
    }
    LaunchedEffect(uiState.currentPage, uiState.isLoading) {
        if (!uiState.isLoading && !uiState.directoryNotFound) onCurrentPage(uiState.currentPage)
    }
    BackHandler(enabled = sidebarOpen || uiState.showModeSelector || uiState.showThumbnailBar) {
        if (sidebarOpen) sidebarOpen = false else viewModel.hideOverlays()
    }

    val showToolbar = (if (twoPageMode) uiState.showModeSelector else uiState.showControls) &&
        !uiState.isLoading && !uiState.directoryNotFound

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF242424))
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
            CompositionLocalProvider(
                LocalGifAutoplay provides sharedPreferences.getBoolean(PreferenceKeys.READER_GIF_AUTOPLAY, true)
            ) {
                Box(
                    Modifier.fillMaxSize().padding(
                        start = sidebarWidth,
                        top = if (showToolbar && uiState.readerMode == ReaderMode.VERTICAL_STRIP) 56.dp else 0.dp
                    )
                ) {
                    ReaderContent(
                        uiState = uiState,
                        spreadLayout = spreadLayout,
                        volumeScroll = volumeScroll,
                        onPageChange = {
                            lastPage = it
                            viewModel.setCurrentPage(it)
                            if (twoPageMode) viewModel.hideOverlays()
                        },
                        onPageJumpHandled = {
                            lastPage = it
                            viewModel.completePageJump(it)
                        },
                        onTap = viewModel::toggleControls
                    )
                }
            }
        }

        val layoutDirection = LocalLayoutDirection.current
        val startSign = if (layoutDirection == LayoutDirection.Ltr) -1 else 1
        AnimatedVisibility(
            visible = sidebarOpen && uiState.imageList.isNotEmpty(),
            enter = slideInHorizontally(tween(300, easing = SlideInEasing::getInterpolation)) { startSign * it },
            exit = slideOutHorizontally(tween(300, easing = SlideOutEasing::getInterpolation)) { startSign * it },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            ReaderPageSidebar(
                images = uiState.imageList,
                currentPage = uiState.currentPage,
                onPageSelected = viewModel::jumpToPage
            )
        }

        if (showToolbar) {
            ReaderTopBar(
                title = uiState.directoryName,
                tabCount = recentTabs.size,
                onRecentTabs = onRecentTabs,
                onNavigateBack = onNavigateBack
            )
        }

        if (!uiState.isLoading && !uiState.directoryNotFound) {
            Box(
                Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(50.dp)
                    .semantics { contentDescription = "Reader mode" }
                    .clickable(role = Role.Button) {
                        sidebarOpen = false
                        viewModel.toggleModeSelector()
                    }
            )
            if (!uiState.showThumbnailBar) {
                Text(
                    text = "Page: $pageLabel / ${uiState.totalPages}",
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(topStart = 4.dp))
                        .background(ReaderChrome)
                        .clickable {
                            sidebarOpen = !sidebarOpen
                            if (sidebarOpen) viewModel.hideOverlays()
                        }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Mode selector (right side)
        AnimatedVisibility(
            visible = uiState.showModeSelector,
            enter = slideInHorizontally(tween(300, easing = SlideInEasing::getInterpolation)) { -startSign * it },
            exit = slideOutHorizontally(tween(300, easing = SlideOutEasing::getInterpolation)) { -startSign * it },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            ReaderModeSelector(
                currentMode = uiState.readerMode,
                onModeSelect = viewModel::setReaderMode
            )
        }

        // Thumbnail bar (bottom)
        AnimatedVisibility(
            visible = uiState.showThumbnailBar,
            enter = slideInVertically(tween(300, easing = SlideInEasing::getInterpolation)) { it },
            exit = slideOutVertically(tween(300, easing = SlideOutEasing::getInterpolation)) { it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            ThumbnailBar(
                images = uiState.imageList,
                currentPage = uiState.currentPage,
                onThumbnailClick = { page ->
                    viewModel.jumpToPage(page)
                    viewModel.hideModeSelector()
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
    volumeScroll: VolumeScroll?,
    onPageChange: (Int) -> Unit,
    onPageJumpHandled: (Int) -> Unit,
    onTap: () -> Unit
) {
    when (uiState.readerMode) {
        ReaderMode.HORIZONTAL_SWIPE, ReaderMode.TWO_PAGE -> key(spreadLayout) {
            HorizontalSwipeReader(
                spreads = spreadLayout,
                images = uiState.imageList,
                currentPage = uiState.currentPage,
                requestedPage = uiState.requestedPage,
                onPageJumpHandled = onPageJumpHandled,
                onPageChange = onPageChange,
                onTap = onTap
            )
        }
        ReaderMode.VERTICAL_SWIPE -> {
            val pagerState = rememberPagerState(
                initialPage = uiState.currentPage,
                pageCount = { uiState.imageList.size }
            )
            val reportPage by rememberUpdatedState(onPageChange)
            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.settledPage }.collect { reportPage(it) }
            }
            LaunchedEffect(uiState.requestedPage) {
                uiState.requestedPage?.let { page ->
                    pagerState.requestScrollToPage(page)
                    onPageJumpHandled(page)
                }
            }
            VerticalSwipeReader(pagerState, uiState.imageList, onTap)
        }
        ReaderMode.VERTICAL_STRIP -> VerticalStripReader(
            images = uiState.imageList,
            initialPage = uiState.currentPage,
            volumeScroll = volumeScroll,
            requestedPage = uiState.requestedPage,
            onPageJumpHandled = onPageJumpHandled,
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
    requestedPage: Int?,
    onPageJumpHandled: (Int) -> Unit,
    onPageChange: (Int) -> Unit,
    onTap: () -> Unit
) {
    val desiredItem = spreads.itemForPage(currentPage)
    val pagerState = rememberPagerState(initialPage = desiredItem, pageCount = { spreads.itemCount })
    val activePage by rememberUpdatedState(currentPage)
    val reportPage by rememberUpdatedState(onPageChange)
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { item ->
            val pages = spreads.pagesForItem(item)
            if (activePage !in pages) reportPage(pages.first)
        }
    }
    LaunchedEffect(requestedPage) {
        requestedPage?.let { page ->
            pagerState.requestScrollToPage(spreads.itemForPage(page))
            onPageJumpHandled(page)
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
                        page = page,
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
            page = page,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun VerticalStripReader(
    images: List<Uri>,
    initialPage: Int,
    volumeScroll: VolumeScroll?,
    requestedPage: Int?,
    onPageJumpHandled: (Int) -> Unit,
    onPageChange: (Int) -> Unit,
    onTap: () -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialPage)
    LaunchedEffect(volumeScroll) {
        volumeScroll?.let { listState.scrollBy(it.pixels) }
    }

    val reportPage by rememberUpdatedState(onPageChange)
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { reportPage(it) }
    }
    LaunchedEffect(requestedPage) {
        requestedPage?.let { page ->
            listState.requestScrollToItem(page)
            onPageJumpHandled(page)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onTap() })
            },
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(images) { index, uri ->
            ReaderImage(
                uri = uri,
                page = index,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}

@Composable
private fun ReaderImage(
    uri: Uri,
    page: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val context = LocalContext.current
    val isGif = uri.lastPathSegment?.endsWith(".gif", ignoreCase = true) == true
    val autoplay = LocalGifAutoplay.current
    var playGif by rememberSaveable(uri) { mutableStateOf(autoplay) }
    val image = remember(context, uri, playGif) {
        ImageRequest.Builder(context).data(uri).apply {
            if (isGif) memoryCacheKey("$uri|animated=$playGif")
            if (isGif && playGif) {
                decoderFactory(if (Build.VERSION.SDK_INT >= 28) ImageDecoderDecoder.Factory() else GifDecoder.Factory())
            }
        }.build()
    }
    val imageModifier = modifier.readerPageTransition(page, thumbnail = false)
    AsyncImage(
        model = image,
        contentDescription = "Page ${page + 1}",
        contentScale = contentScale,
        modifier = if (isGif) imageModifier.clickable(
            onClickLabel = if (playGif) "Pause GIF" else "Play GIF",
            onClick = { playGif = !playGif }
        ) else imageModifier
    )
}

@Composable
private fun ReaderTopBar(
    title: String,
    tabCount: Int,
    onRecentTabs: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().height(56.dp).background(ReaderChrome.copy(alpha = 0.76f)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
        }
        Text(
            title,
            Modifier.weight(1f).padding(horizontal = 8.dp),
            color = Color.White,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Box(
            Modifier.padding(end = 8.dp).size(40.dp, 38.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(1.dp, Color.White, RoundedCornerShape(6.dp))
                .semantics { contentDescription = "Reader tabs" }
                .clickable(role = Role.Button, onClick = onRecentTabs),
            contentAlignment = Alignment.Center
        ) {
            Text(tabCount.toString(), color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
private fun ReaderModeSelector(
    currentMode: ReaderMode,
    onModeSelect: (ReaderMode) -> Unit
) {
    Column(
        modifier = Modifier.width(112.dp).verticalScroll(rememberScrollState())
    ) {
        ReaderModeButton(
            icon = Icons.Default.SwapHoriz,
            label = "Swipe\nhorizontally",
            isSelected = currentMode == ReaderMode.HORIZONTAL_SWIPE,
            onClick = { onModeSelect(ReaderMode.HORIZONTAL_SWIPE) }
        )
        ReaderModeButton(
            icon = Icons.Default.SwapVert,
            label = "Swipe\nvertically",
            isSelected = currentMode == ReaderMode.VERTICAL_SWIPE,
            onClick = { onModeSelect(ReaderMode.VERTICAL_SWIPE) }
        )
        ReaderModeButton(
            icon = Icons.Default.ViewList,
            label = "Scroll\nvertically",
            isSelected = currentMode == ReaderMode.VERTICAL_STRIP,
            onClick = { onModeSelect(ReaderMode.VERTICAL_STRIP) }
        )
        ReaderModeButton(
            icon = Icons.Default.ViewColumn,
            label = "Two-page",
            isSelected = currentMode == ReaderMode.TWO_PAGE,
            onClick = { onModeSelect(ReaderMode.TWO_PAGE) }
        )
    }
}

@Composable
private fun ReaderModeButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(if (isSelected) Color(0xC3B33E5C) else Color(0xC35A5757))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(top = 16.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            lineHeight = 17.sp,
            minLines = 2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun ReaderPageSidebar(
    images: List<Uri>,
    currentPage: Int,
    onPageSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = currentPage)
    LaunchedEffect(currentPage) {
        if (currentPage in images.indices &&
            listState.layoutInfo.visibleItemsInfo.none { it.index == currentPage }
        ) {
            listState.requestScrollToItem(currentPage)
        }
    }
    LazyColumn(
        modifier = Modifier.width(76.dp).fillMaxHeight().background(MaterialTheme.colorScheme.surface),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(top = 56.dp, bottom = 8.dp)
    ) {
        itemsIndexed(images) { page, uri ->
            ThumbnailItem(uri, page == currentPage, page + 1) { onPageSelected(page) }
        }
    }
}

@Composable
private fun ThumbnailBar(
    images: List<Uri>,
    currentPage: Int,
    onThumbnailClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    LaunchedEffect(currentPage, images.size) {
        if (currentPage in images.indices) listState.requestScrollToItem(currentPage)
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
