package com.flamyoad.tsukiviewer.ui.screens.doujin

import android.app.Application
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed as lazyItemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.repository.BookmarkRepository
import com.flamyoad.tsukiviewer.core.repository.MetadataRepository
import com.flamyoad.tsukiviewer.ui.screens.doujin.components.BookmarkDialog
import com.flamyoad.tsukiviewer.ui.screens.doujin.components.TagGroup
import com.flamyoad.tsukiviewer.ui.theme.ColorPrimaryLight
import com.flamyoad.tsukiviewer.ui.theme.SubLightTextColor
import com.flamyoad.tsukiviewer.ui.theme.TextWhite
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme
import com.flamyoad.tsukiviewer.ui.theme.WineRed
import kotlinx.coroutines.launch

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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    
    // Create repositories and ViewModel
    val db = remember { AppDatabase.getInstance(context) }
    val metadataRepo = remember {
        MetadataRepository(
            context = context,
            db = db,
            pathDao = db.includedFolderDao(),
            doujinDetailsDao = db.doujinDetailsDao(),
            tagDao = db.tagsDao(),
            doujinTagDao = db.doujinTagDao(),
            folderDao = db.folderDao()
        )
    }
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
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    
    // Load doujin details when path changes
    LaunchedEffect(doujinPath) {
        viewModel.loadDoujinDetails(doujinPath)
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
                title = {
                    Text(
                        text = uiState.shortTitleEnglish.ifEmpty { uiState.fullTitleEnglish.ifEmpty { "Doujin Details" } },
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
                    IconButton(onClick = { viewModel.loadBookmarkGroups() }) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Add to Collection"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                windowInsets = WindowInsets(
                    top = 0.dp,
                    bottom = 0.dp,
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onReadClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                },
                text = { Text("Read") }
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
                    containerColor = ColorPrimaryLight,
                    contentColor = TextWhite
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
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
                            modifier = Modifier.fillMaxSize()
                        )
                        DoujinTab.Images -> DoujinImagesContent(
                            uiState = uiState,
                            onImageClick = onImageClick,
                            onGridStyleChange = { viewModel.setGridViewStyle(it) },
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
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Cover image with background blur
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // Blurred background
            uiState.coverImage?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(20.dp)
                )
            }
            
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                            )
                        )
                    )
            )
            
            // Cover image
            uiState.coverImage?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Cover",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                        .aspectRatio(0.7f)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
        
        // Title section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // English title
                Text(
                    text = uiState.fullTitleEnglish,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                // Japanese title
                if (uiState.fullTitleJapanese.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.fullTitleJapanese,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Info section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                InfoRow(
                    icon = Icons.Default.Image,
                    label = "Pages",
                    value = uiState.imageCount.toString()
                )
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Modified",
                    value = uiState.dateModified
                )
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    icon = Icons.Default.Folder,
                    label = "Path",
                    value = uiState.directoryPath
                )
            }
        }
        
        // Tags section
        if (uiState.hasMetadata && !uiState.tags.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Tags",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    TagGroup(
                        label = "Parodies",
                        tags = uiState.tags.parodies,
                        onTagClick = onTagClick
                    )
                    TagGroup(
                        label = "Characters",
                        tags = uiState.tags.characters,
                        onTagClick = onTagClick
                    )
                    TagGroup(
                        label = "Tags",
                        tags = uiState.tags.tags,
                        onTagClick = onTagClick
                    )
                    TagGroup(
                        label = "Artists",
                        tags = uiState.tags.artists,
                        onTagClick = onTagClick
                    )
                    TagGroup(
                        label = "Groups",
                        tags = uiState.tags.groups,
                        onTagClick = onTagClick
                    )
                    TagGroup(
                        label = "Languages",
                        tags = uiState.tags.languages,
                        onTagClick = onTagClick
                    )
                    TagGroup(
                        label = "Categories",
                        tags = uiState.tags.categories,
                        onTagClick = onTagClick
                    )
                }
            }
        } else if (!uiState.hasMetadata) {
            // No metadata indicator
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No metadata found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Sync to fetch title and tags",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Bottom spacing for FAB
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun DoujinImagesContent(
    uiState: DoujinDetailsUiState,
    onImageClick: (Int) -> Unit,
    onGridStyleChange: (GridViewStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    var showViewStyleMenu by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        // View style selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${uiState.imageList.size} images",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Box {
                IconButton(onClick = { showViewStyleMenu = true }) {
                    Icon(
                        imageVector = when (uiState.gridViewStyle) {
                            GridViewStyle.Grid, GridViewStyle.Scaled -> Icons.Default.GridView
                            GridViewStyle.Row, GridViewStyle.List -> Icons.Default.ViewList
                        },
                        contentDescription = "Change view style"
                    )
                }
                
                DropdownMenu(
                    expanded = showViewStyleMenu,
                    onDismissRequest = { showViewStyleMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Grid") },
                        onClick = {
                            onGridStyleChange(GridViewStyle.Grid)
                            showViewStyleMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.GridView, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Scaled") },
                        onClick = {
                            onGridStyleChange(GridViewStyle.Scaled)
                            showViewStyleMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.GridView, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Row") },
                        onClick = {
                            onGridStyleChange(GridViewStyle.Row)
                            showViewStyleMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.ViewList, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("List") },
                        onClick = {
                            onGridStyleChange(GridViewStyle.List)
                            showViewStyleMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.ViewList, contentDescription = null)
                        }
                    )
                }
            }
        }
        
        // Image grid/list based on style
        when (uiState.gridViewStyle) {
            GridViewStyle.Grid -> ImageGrid(
                images = uiState.imageList,
                columns = 3,
                aspectRatio = 0.7f,
                onImageClick = onImageClick
            )
            GridViewStyle.Scaled -> ImageGrid(
                images = uiState.imageList,
                columns = 2,
                aspectRatio = 0.7f,
                onImageClick = onImageClick
            )
            GridViewStyle.Row -> ImageRow(
                images = uiState.imageList,
                onImageClick = onImageClick
            )
            GridViewStyle.List -> ImageList(
                images = uiState.imageList,
                onImageClick = onImageClick
            )
        }
    }
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
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(images) { index, uri ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)
                    .combinedClickable(
                        onClick = { onImageClick(index) }
                    ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Page ${index + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Page number overlay
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                RoundedCornerShape(topStart = 4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageRow(
    images: List<Uri>,
    onImageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        lazyItemsIndexed(images) { index, uri ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { onImageClick(index) }
                    ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Page ${index + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                    )
                    
                    Text(
                        text = "Page ${index + 1}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageList(
    images: List<Uri>,
    onImageClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        lazyItemsIndexed(images) { index, uri ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { onImageClick(index) }
                    ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Page ${index + 1}",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text(
                        text = "Page ${index + 1}",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
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
