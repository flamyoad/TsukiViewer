package com.flamyoad.tsukiviewer.ui.screens.tags

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.model.DoujinDetails
import com.flamyoad.tsukiviewer.core.model.Tag
import com.flamyoad.tsukiviewer.ui.screens.home.DoujinUiModel
import com.flamyoad.tsukiviewer.ui.screens.home.ViewMode
import com.flamyoad.tsukiviewer.ui.screens.home.components.DoujinGridItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class TagDetailsUiState(
    val isLoading: Boolean = true,
    val tag: Tag? = null,
    val doujinList: List<DoujinUiModel> = emptyList()
)

class TagDetailsViewModel(
    private val application: Application,
    private val tagId: Long
) : ViewModel() {
    
    private val db = AppDatabase.getInstance(application)
    private val tagDao = db.tagsDao()
    private val collectionDoujinDao = db.collectionDoujinDao()
    
    private val _uiState = MutableStateFlow(TagDetailsUiState())
    val uiState: StateFlow<TagDetailsUiState> = _uiState.asStateFlow()
    
    init {
        loadTagDetails()
    }
    
    private fun loadTagDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Get all tags and find the one with matching ID
                val allTags = db.tagsDao().getAllBlocking()
                val tag = allTags.find { it.tagId == tagId }
                
                if (tag != null) {
                    // Search for doujins with this tag
                    val doujinDetails = collectionDoujinDao.searchIncludedOr(listOf(tagId))
                    
                    val doujinList = doujinDetails.mapNotNull { details ->
                        val coverImage = findCoverImage(details.absolutePath)
                        if (coverImage != null) {
                            DoujinUiModel(
                                id = details.id ?: 0L,
                                coverUri = android.net.Uri.fromFile(coverImage),
                                title = details.shortTitleEnglish.ifEmpty { 
                                    details.fullTitleEnglish.ifEmpty { 
                                        details.absolutePath.name 
                                    } 
                                },
                                numberOfItems = 0,
                                lastModified = details.absolutePath.lastModified(),
                                path = details.absolutePath.absolutePath,
                                isSelected = false
                            )
                        } else null
                    }
                    
                    _uiState.value = TagDetailsUiState(
                        isLoading = false,
                        tag = tag,
                        doujinList = doujinList
                    )
                } else {
                    _uiState.value = TagDetailsUiState(isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value = TagDetailsUiState(isLoading = false)
            }
        }
    }
    
    private fun findCoverImage(directory: File): File? {
        if (!directory.exists() || !directory.isDirectory) return null
        
        val imageExtensions = listOf("jpg", "jpeg", "png", "gif", "webp")
        val files = directory.listFiles { file ->
            file.isFile && imageExtensions.any { ext -> 
                file.extension.equals(ext, ignoreCase = true) 
            }
        }?.sortedBy { it.name }
        
        return files?.firstOrNull()
    }
    
    class Factory(
        private val application: Application,
        private val tagId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TagDetailsViewModel::class.java)) {
                return TagDetailsViewModel(application, tagId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagDetailsScreen(
    tagId: Long,
    onNavigateBack: () -> Unit,
    onDoujinClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    
    val viewModel: TagDetailsViewModel = viewModel(
        factory = remember { TagDetailsViewModel.Factory(application, tagId) }
    )
    
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = uiState.tag?.let { "${it.type}: ${it.name}" } ?: "Tag Details"
                    ) 
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
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                windowInsets = WindowInsets(
                    top = 0.dp,
                    bottom = 0.dp
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.doujinList.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No doujins found with this tag",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(
                        items = uiState.doujinList,
                        key = { it.path }
                    ) { doujin ->
                        DoujinGridItem(
                            doujin = doujin,
                            viewMode = ViewMode.NORMAL_GRID,
                            onClick = { onDoujinClick(doujin.path) },
                            onLongClick = { /* No selection mode for tag details */ }
                        )
                    }
                }
            }
        }
    }
}
