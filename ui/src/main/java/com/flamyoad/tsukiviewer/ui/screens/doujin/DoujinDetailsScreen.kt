package com.flamyoad.tsukiviewer.ui.screens.doujin

import android.app.Application
import android.content.res.Configuration
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.animation.EnterExitState
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.flamyoad.tsukiviewer.ui.navigation.LocalReaderReturnAnchor
import com.flamyoad.tsukiviewer.ui.navigation.readerPageTransition
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed as lazyItemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.flamyoad.tsukiviewer.ui.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.preference.PreferenceManager
import com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys
import coil.compose.AsyncImage
import com.flamyoad.tsukiviewer.core.R as CoreR
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.repository.BookmarkRepository
import com.flamyoad.tsukiviewer.core.repository.RelatedManga
import com.flamyoad.tsukiviewer.core.repository.RelatedMangaRepository
import com.flamyoad.tsukiviewer.core.utils.openMangaFolder
import com.flamyoad.tsukiviewer.ui.screens.doujin.components.BookmarkDialog
import com.flamyoad.tsukiviewer.ui.screens.doujin.components.TagGroup
import com.flamyoad.tsukiviewer.ui.theme.SubLightTextColor
import com.flamyoad.tsukiviewer.ui.theme.TextWhite
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme
import com.flamyoad.tsukiviewer.ui.theme.WineRed
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.produceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private enum class DoujinTab(val title: String) {
    Details("Details"),
    Images("Images")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DoujinDetailsScreen(
    doujinPath: String,
    onNavigateBack: () -> Unit,
    onReadClick: () -> Unit,
    onImageClick: (Int) -> Unit = {},
    onTagClick: (TagUiModel) -> Unit = {},
    modifier: Modifier = Modifier,
    onEditMetadata: () -> Unit = {},
    onFetchMetadata: () -> Unit = {},
    onRelatedMangaClick: (String) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    var showGridStyles by remember { mutableStateOf(false) }
    var showMoreActions by remember { mutableStateOf(false) }
    
    // Create repositories and ViewModel
    val db = remember { AppDatabase.getInstance(context) }
    val metadataRepo = remember(db) { metadataRepository(application, db) }
    val bookmarkRepo = remember {
        BookmarkRepository(
            db = db,
            groupDao = db.bookmarkGroupDao(),
            itemDao = db.bookmarkItemDao()
        )
    }
    
    val viewModel: DoujinDetailsViewModel = viewModel(
        factory = DoujinDetailsViewModelFactory(application, metadataRepo, bookmarkRepo)
    )
    
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val tabs = DoujinTab.entries
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val relatedRepository = remember(db) { RelatedMangaRepository(db) }
    val preferences = remember(application) { PreferenceManager.getDefaultSharedPreferences(application) }
    val pagerState = rememberPagerState(
        initialPage = if (preferences.getString(PreferenceKeys.DOUJIN_DETAILS_LANDING_SCR, null) == "ImageGrid") 1 else 0,
        pageCount = { tabs.size }
    )
    var relatedManga by remember(doujinPath) { mutableStateOf(emptyList<RelatedManga>()) }
    LaunchedEffect(lifecycle, doujinPath) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refreshMetadata(doujinPath)
            relatedRepository.observe(File(doujinPath), preferences).collect { relatedManga = it }
        }
    }
    
    // Load doujin details when path changes
    LaunchedEffect(doujinPath) {
        viewModel.setGridViewStyle(GridViewStyle.fromPreference(
            preferences.getString(PreferenceKeys.DEFAULT_GRID_VIEW_STYLE, null)
        ))
        viewModel.loadDoujinDetails(doujinPath)
    }
    
    fun openPage(index: Int, fromGrid: Boolean) {
        if (!preferences.getBoolean(PreferenceKeys.USE_EXTERNAL_GALLERY, false)) {
            if (fromGrid) onImageClick(index) else onReadClick()
            return
        }
        val path = uiState.imageList.getOrNull(index)?.path ?: return
        val target = preferences.getString(PreferenceKeys.EXTERNAL_GALLERY_PKG_NAME, null)
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(path))
            context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "image/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                target?.takeIf { it.isNotBlank() }?.let { setPackage(it) }
            })
        } catch (_: Exception) {
            Toast.makeText(context, "Can't open the external application", Toast.LENGTH_SHORT).show()
        }
    }

    // Show snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }
    
    // Handle directory not found
    LaunchedEffect(uiState.directoryNotFound) {
        if (uiState.directoryNotFound) {
            onNavigateBack()
        }
    }
    
    // Show bookmark dialog
    if (uiState.showBookmarkDialog) {
        BookmarkDialog(
            bookmarkGroups = uiState.bookmarkGroups,
            onGroupToggle = { viewModel.toggleBookmarkGroup(it) },
            onCreateGroup = { viewModel.createNewBookmarkGroup(it) },
            onDismiss = { viewModel.dismissBookmarkDialog() },
            onConfirm = { viewModel.saveBookmarkSelections() }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                expandedHeight = 56.dp,
                title = {
                    Text(
                        text = "Doujin Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Color.White,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (pagerState.currentPage == DoujinTab.Images.ordinal) {
                        Box {
                            IconButton(onClick = { showGridStyles = true }) {
                                Icon(Icons.Default.ViewList, "Change view style")
                            }
                            DropdownMenu(expanded = showGridStyles, onDismissRequest = { showGridStyles = false }) {
                                GridViewStyle.entries.forEach { style ->
                                    DropdownMenuItem(text = { Text(style.name) }, onClick = {
                                        viewModel.setGridViewStyle(style)
                                        showGridStyles = false
                                    })
                                }
                            }
                        }
                    } else {
                        IconButton(onClick = onFetchMetadata) { Icon(Icons.Default.Sync, "Fetch metadata") }
                        IconButton(onClick = onEditMetadata) { Icon(Icons.Default.Edit, "Edit tags") }
                    }
                    Box {
                        IconButton(onClick = { showMoreActions = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More actions")
                        }
                        DropdownMenu(
                            expanded = showMoreActions,
                            onDismissRequest = { showMoreActions = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Read") },
                                onClick = {
                                    showMoreActions = false
                                    openPage(0, fromGrid = false)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(CoreR.string.open_manga_folder)) },
                                onClick = {
                                    showMoreActions = false
                                    openMangaFolder(context, doujinPath)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                windowInsets = WindowInsets(
                    top = 0.dp,
                    bottom = 0.dp,
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Tab Row
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = TextWhite,
                    divider = {},
                    indicator = { positions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(positions[pagerState.currentPage]),
                            height = 2.dp,
                            color = Color.White
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            modifier = Modifier.height(56.dp),
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { 
                                Text(
                                    text = tab.title,
                                    color = if (pagerState.currentPage == index) TextWhite else SubLightTextColor
                                ) 
                            },
                            selectedContentColor = TextWhite,
                            unselectedContentColor = SubLightTextColor
                        )
                    }
                }
                
                // Pager Content
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (tabs[page]) {
                        DoujinTab.Details -> DoujinDetailsContent(
                            uiState = uiState,
                            onTagClick = onTagClick,
                            relatedManga = relatedManga,
                            showRelatedManga = preferences.getBoolean(PreferenceKeys.SHOW_RELATED_MANGA, true),
                            onBookmark = viewModel::loadBookmarkGroups,
                            onRead = { openPage(0, fromGrid = false) },
                            onRelatedMangaClick = onRelatedMangaClick,
                            modifier = Modifier.fillMaxSize()
                        )
                        DoujinTab.Images -> DoujinImagesContent(
                            uiState = uiState,
                            onImageClick = { openPage(it, fromGrid = true) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DoujinDetailsContent(
    uiState: DoujinDetailsUiState,
    onTagClick: (TagUiModel) -> Unit,
    modifier: Modifier = Modifier,
    relatedManga: List<RelatedManga> = emptyList(),
    showRelatedManga: Boolean = true,
    onBookmark: () -> Unit = {},
    onRead: () -> Unit = {},
    onRelatedMangaClick: (String) -> Unit = {}
) {
    Column(modifier.verticalScroll(rememberScrollState()).padding(bottom = 32.dp)) {
        Box(Modifier.fillMaxWidth().height(210.dp).zIndex(1f)) {
            AsyncImage(
                model = uiState.coverImage,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.3f,
                modifier = Modifier.fillMaxSize()
            )
            Row(Modifier.fillMaxWidth().padding(top = 8.dp, end = 8.dp)) {
                AsyncImage(
                    model = uiState.coverImage,
                    contentDescription = "Read",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.width(150.dp).height(192.dp).clickable(onClick = onRead)
                )
                Column(Modifier.weight(1f)) {
                    Text(uiState.fullTitleEnglish, fontSize = 14.sp, color = Color.Black)
                    if (uiState.fullTitleJapanese.isNotEmpty()) {
                        Text(uiState.fullTitleJapanese, Modifier.padding(top = 8.dp), fontSize = 14.sp)
                    }
                }
            }
            FloatingActionButton(
                onClick = onBookmark,
                shape = CircleShape,
                containerColor = Color(0xFFED2553),
                contentColor = Color.White,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp).offset(y = 16.dp)
            ) {
                Icon(Icons.Default.Favorite, "Add to bookmarks")
            }
        }
        DetailsSectionHeader("Tags")
        if (uiState.tags.isEmpty()) {
            Column(
                Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painterResource(R.drawable.gallery_tags_empty),
                    contentDescription = null,
                    modifier = Modifier.width(145.dp).height(85.dp)
                )
                Text("No tags found :(", Modifier.padding(top = 8.dp), color = Color(0xFFA0A8C0), fontSize = 14.sp)
            }
        } else {
            Column(Modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
                TagGroup("Parodies", uiState.tags.parodies, onTagClick)
                TagGroup("Characters", uiState.tags.characters, onTagClick)
                TagGroup("Tags", uiState.tags.tags, onTagClick)
                TagGroup("Artists", uiState.tags.artists, onTagClick)
                TagGroup("Groups", uiState.tags.groups, onTagClick)
                TagGroup("Languages", uiState.tags.languages, onTagClick)
                TagGroup("Categories", uiState.tags.categories, onTagClick)
            }
        }
        if (showRelatedManga) {
            DetailsSectionHeader("Related manga")
            if (relatedManga.isEmpty()) {
                Text("No related manga found in your library.", Modifier.padding(8.dp), fontSize = 14.sp)
            } else {
                RelatedMangaRow(relatedManga, onRelatedMangaClick)
            }
            Spacer(Modifier.height(16.dp))
        }
        DetailsSectionHeader("Folder details")
        Row(
            Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.Folder, contentDescription = null, tint = Color(0xFFF9C650))
            Text(uiState.directoryPath, fontSize = 14.sp, lineHeight = 16.sp)
        }
        Row(Modifier.fillMaxWidth().padding(top = 24.dp)) {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(uiState.dateModified, color = WineRed, fontSize = 14.sp)
                Text("Date Modified", Modifier.padding(top = 8.dp), color = Color.Gray, fontSize = 12.sp)
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(uiState.imageCount.toString(), color = WineRed, fontSize = 14.sp)
                Text("Pages", Modifier.padding(top = 8.dp), color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun DetailsSectionHeader(title: String) {
    Text(
        title.uppercase(),
        modifier = Modifier.fillMaxWidth().background(Color(0xFF5D2A42)).padding(8.dp),
        color = Color.White,
        fontSize = 14.sp,
        letterSpacing = 1.sp
    )
}

@Composable
private fun DoujinImagesContent(
    uiState: DoujinDetailsUiState,
    onImageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    when (uiState.gridViewStyle) {
        GridViewStyle.Grid -> ImageGrid(uiState.imageList, if (landscape) 5 else 3, 1f, onImageClick, modifier)
        GridViewStyle.Scaled -> ImageGrid(uiState.imageList, if (landscape) 3 else 2, 1f / 1.6f, onImageClick, modifier)
        GridViewStyle.Row -> ImageList(uiState.imageList, onImageClick, modifier, showFileInfo = false)
        GridViewStyle.List -> ImageList(uiState.imageList, onImageClick, modifier, showFileInfo = true)
    }
}

@Composable
private fun returningThumbnailPage(): Int? {
    val anchor = LocalReaderReturnAnchor.current ?: return null
    val transition = LocalNavAnimatedContentScope.current.transition
    return anchor.page.takeIf { transition.targetState == EnterExitState.Visible }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageGrid(
    images: List<Uri>,
    columns: Int,
    aspectRatio: Float,
    onImageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberLazyGridState()
    val targetPage = returningThumbnailPage()
    LaunchedEffect(targetPage, images.size) {
        if (targetPage != null && targetPage in images.indices &&
            state.layoutInfo.visibleItemsInfo.none { it.index == targetPage }
        ) state.requestScrollToItem(targetPage)
    }
    LazyVerticalGrid(
        state = state,
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(images) { index, uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Page ${index + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(aspectRatio)
                    .clickable { onImageClick(index) }
                    .readerPageTransition(index, thumbnail = true)
            )
        }
    }
}

@Composable
private fun ImageList(
    images: List<Uri>,
    onImageClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    showFileInfo: Boolean
) {
    val state = rememberLazyListState()
    val targetPage = returningThumbnailPage()
    LaunchedEffect(targetPage, images.size) {
        if (targetPage != null && targetPage in images.indices &&
            state.layoutInfo.visibleItemsInfo.none { it.index == targetPage }
        ) state.requestScrollToItem(targetPage)
    }
    LazyColumn(state = state, modifier = modifier.fillMaxSize()) {
        lazyItemsIndexed(images) { index, uri ->
            if (showFileInfo) {
                val modified by produceState("", uri) {
                    value = withContext(Dispatchers.IO) {
                        uri.path?.let { path ->
                            val time = File(path).lastModified()
                            if (time > 0L) SimpleDateFormat("d MMM, yyyy h:mm:ss a", Locale.getDefault())
                                .format(Date(time)) else ""
                        }.orEmpty()
                    }
                }
                Row(
                    Modifier.fillMaxWidth().height(125.dp)
                        .background(Color(0xFF282A36))
                        .clickable { onImageClick(index) }
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Page ${index + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.width(100.dp).height(125.dp)
                            .readerPageTransition(index, thumbnail = true)
                    )
                    Column(Modifier.padding(8.dp)) {
                        Text(
                            uri.lastPathSegment.orEmpty(),
                            color = Color(0xFFEDECEC),
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(modified, Modifier.padding(top = 4.dp), color = Color(0xFF959595), fontSize = 14.sp)
                    }
                }
            } else {
                AsyncImage(
                    model = uri,
                    contentDescription = "Page ${index + 1}",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1.4f)
                        .clickable { onImageClick(index) }
                        .readerPageTransition(index, thumbnail = true)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DoujinDetailsScreenPreview() {
    TsukiViewerTheme {
        DoujinDetailsContent(
            uiState = DoujinDetailsUiState(
                isLoading = false,
                directoryPath = "/storage/emulated/0/Doujins/Sample",
                fullTitleEnglish = "[Circle Name] Sample Doujin Title (Parody)",
                shortTitleEnglish = "Sample Doujin Title",
                fullTitleJapanese = "サンプル同人誌タイトル",
                imageCount = 24,
                dateModified = "Jan 15, 2024 14:30",
                hasMetadata = true,
                tags = DoujinTagsUiModel(
                    parodies = listOf(
                        TagUiModel(1, "azur lane", "parody", count = 5234)
                    ),
                    characters = listOf(
                        TagUiModel(2, "enterprise", "character", count = 432)
                    ),
                    tags = listOf(
                        TagUiModel(3, "sole female", "tag", count = 12345),
                        TagUiModel(4, "sole male", "tag", count = 11234)
                    )
                )
            ),
            onTagClick = {}
        )
    }
}
