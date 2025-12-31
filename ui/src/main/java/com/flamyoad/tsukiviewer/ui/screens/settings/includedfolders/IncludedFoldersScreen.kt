package com.flamyoad.tsukiviewer.ui.screens.settings.includedfolders

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flamyoad.tsukiviewer.ui.screens.settings.includedfolders.components.IncludedFolderItem
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

/**
 * Screen for managing included folders that will be scanned for doujins.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncludedFoldersScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: IncludedFoldersViewModel = viewModel(
        factory = IncludedFoldersViewModelFactory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    
    var showDeleteConfirmDialog by remember { mutableStateOf<IncludedFolderUiModel?>(null) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    
    // Document tree picker for selecting folders
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let { selectedUri ->
            // Convert content URI to file path
            // Note: This may need adjustment based on actual URI format
            val path = getPathFromUri(selectedUri)
            if (path != null) {
                viewModel.addFolder(path)
            } else {
                // Fallback: try to extract path from URI
                val uriPath = selectedUri.path
                if (uriPath != null) {
                    // Handle document tree URI path format
                    val cleanPath = uriPath
                        .replace("/tree/primary:", "/storage/emulated/0/")
                        .replace("/tree/", "/storage/")
                        .replace(":", "/")
                    viewModel.addFolder(cleanPath)
                }
            }
        }
    }
    
    // Show snackbar when message is available
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }
    
    // Show permission dialog if needed
    LaunchedEffect(uiState.needsManageStoragePermission) {
        if (uiState.needsManageStoragePermission) {
            showPermissionDialog = true
        }
    }
    
    // Permission dialog for Android 11+
    if (showPermissionDialog && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Storage Permission Required") },
            text = {
                Text(
                    "This app needs access to all files to browse your image folders. " +
                    "Please grant 'All files access' permission."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                            data = Uri.parse("package:${context.packageName}")
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Delete confirmation dialog
    showDeleteConfirmDialog?.let { folder ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Remove Folder") },
            text = {
                Text("Are you sure you want to remove \"${folder.displayName}\" from included folders?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeFolder(folder.path)
                        showDeleteConfirmDialog = null
                    }
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Included Folders") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
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
            FloatingActionButton(
                onClick = {
                    folderPickerLauncher.launch(null)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add folder"
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.folders.isEmpty() -> {
                EmptyFoldersContent(
                    onAddFolderClick = { folderPickerLauncher.launch(null) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = uiState.folders,
                        key = { it.id }
                    ) { folder ->
                        IncludedFolderItem(
                            folder = folder,
                            onDeleteClick = { showDeleteConfirmDialog = folder }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFoldersContent(
    onAddFolderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CreateNewFolder,
            contentDescription = null,
            modifier = Modifier.padding(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Folders Added",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Add folders containing your manga/doujin collections to start browsing.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onAddFolderClick) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Add Folder")
        }
    }
}

/**
 * Attempts to extract a file path from a content URI.
 * Note: This is a simplified implementation. In production, you may need
 * to handle more URI formats.
 */
private fun getPathFromUri(uri: Uri): String? {
    val path = uri.path ?: return null
    
    // Handle document tree URI format like /tree/primary:Download/Manga
    return when {
        path.startsWith("/tree/primary:") -> {
            "/storage/emulated/0/" + path.removePrefix("/tree/primary:")
        }
        path.startsWith("/tree/") -> {
            // Handle external SD card or other storage
            val parts = path.removePrefix("/tree/").split(":", limit = 2)
            if (parts.size == 2) {
                "/storage/${parts[0]}/${parts[1]}"
            } else {
                null
            }
        }
        else -> null
    }
}

@Preview(showBackground = true)
@Composable
private fun IncludedFoldersScreenPreview() {
    TsukiViewerTheme {
        EmptyFoldersContent(
            onAddFolderClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
