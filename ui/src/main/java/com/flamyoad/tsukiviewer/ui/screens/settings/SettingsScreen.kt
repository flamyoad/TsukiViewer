package com.flamyoad.tsukiviewer.ui.screens.settings

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flamyoad.tsukiviewer.ui.screens.settings.components.ClickablePreference
import com.flamyoad.tsukiviewer.ui.screens.settings.components.SettingsCategory
import com.flamyoad.tsukiviewer.ui.screens.settings.components.SwitchPreference
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToIncludedFolders: () -> Unit = {},
    onRestartApp: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    
    var showReaderModeDialog by remember { mutableStateOf(false) }
    var showGridStyleDialog by remember { mutableStateOf(false) }
    var showViewModeDialog by remember { mutableStateOf(false) }
    
    // Show snackbar when message is available
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }
    
    // Reader mode dialog
    if (showReaderModeDialog) {
        ListSelectionDialog(
            title = "Default Reader Mode",
            options = ReaderMode.entries.map { it.displayName },
            selectedIndex = uiState.defaultReaderMode.ordinal,
            onSelect = { index ->
                viewModel.setDefaultReaderMode(ReaderMode.entries[index])
                showReaderModeDialog = false
            },
            onDismiss = { showReaderModeDialog = false }
        )
    }
    
    // Grid style dialog
    if (showGridStyleDialog) {
        ListSelectionDialog(
            title = "Default Grid View Style",
            options = GridViewStyle.entries.map { it.displayName },
            selectedIndex = uiState.defaultGridViewStyle.ordinal,
            onSelect = { index ->
                viewModel.setDefaultGridViewStyle(GridViewStyle.entries[index])
                showGridStyleDialog = false
            },
            onDismiss = { showGridStyleDialog = false }
        )
    }
    
    // View mode dialog
    if (showViewModeDialog) {
        ListSelectionDialog(
            title = "Doujin View Mode",
            options = DoujinViewMode.entries.map { it.displayName },
            selectedIndex = uiState.doujinViewMode.ordinal,
            onSelect = { index ->
                viewModel.setDoujinViewMode(DoujinViewMode.entries[index])
                showViewModeDialog = false
            },
            onDismiss = { showViewModeDialog = false }
        )
    }
    
    // Gallery picker dialog
    if (uiState.showGalleryPickerDialog) {
        GalleryPickerDialog(
            availableApps = emptyList(), // TODO: Load from viewModel
            onSelectApp = { packageName ->
                // viewModel.selectGalleryApp(packageName)
            },
            onDismiss = { /* viewModel.hideGalleryPicker() */ }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                    .verticalScroll(scrollState)
            ) {
                // General Settings
                SettingsCategory(title = "General") {
                    ClickablePreference(
                        title = "Included Folders",
                        summary = "Manage folders to scan for doujins",
                        onClick = onNavigateToIncludedFolders
                    )
                    
                    HorizontalDivider()
                    
                    SwitchPreference(
                        title = "Use new Compose UI",
                        summary = if (uiState.useComposeUi) "Using modern Compose screens" else "Using classic XML views",
                        checked = uiState.useComposeUi,
                        onCheckedChange = { 
                            viewModel.setUseComposeUi(it)
                            // WORKAROUND: need to fix later, restart to ensure SharedPreferences is committed
                            Handler(Looper.getMainLooper()).postDelayed({
                                onRestartApp()
                            }, 1000)
                        }
                    )
                    
                    HorizontalDivider()
                    
                    SwitchPreference(
                        title = "Confirm before quit",
                        summary = "Show confirmation dialog when pressing back",
                        checked = uiState.confirmBeforeQuit,
                        onCheckedChange = { viewModel.setConfirmBeforeQuit(it) }
                    )
                    
                    HorizontalDivider()
                    
                    SwitchPreference(
                        title = "Windows Explorer sorting",
                        summary = "Sort files like Windows Explorer",
                        checked = uiState.useWindowsExplorerSort,
                        onCheckedChange = { viewModel.setUseWindowsExplorerSort(it) }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Display Settings
                SettingsCategory(title = "Display") {
                    ClickablePreference(
                        title = "Default Grid View Style",
                        summary = uiState.defaultGridViewStyle.displayName,
                        onClick = { showGridStyleDialog = true }
                    )
                    
                    HorizontalDivider()
                    
                    ClickablePreference(
                        title = "Doujin View Mode",
                        summary = uiState.doujinViewMode.displayName,
                        onClick = { showViewModeDialog = true }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Reader Settings
                SettingsCategory(title = "Reader") {
                    ClickablePreference(
                        title = "Default Reader Mode",
                        summary = uiState.defaultReaderMode.displayName,
                        onClick = { showReaderModeDialog = true }
                    )
                    
                    HorizontalDivider()
                    
                    SwitchPreference(
                        title = "Scroll by volume buttons",
                        summary = "Use volume buttons to navigate pages",
                        checked = uiState.scrollByVolumeButtons,
                        onCheckedChange = { viewModel.setScrollByVolumeButtons(it) }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // External Gallery Settings
                SettingsCategory(title = "External Gallery") {
                    SwitchPreference(
                        title = "Use external gallery",
                        summary = "Open images in an external app",
                        checked = uiState.useExternalGallery,
                        onCheckedChange = { viewModel.setUseExternalGallery(it) }
                    )
                    
                    HorizontalDivider()
                    
                    ClickablePreference(
                        title = "External image viewer",
                        summary = uiState.externalGalleryPackage.ifEmpty { "Not set" },
                        onClick = { viewModel.showGalleryPicker() },
                        enabled = uiState.useExternalGallery
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // About
                SettingsCategory(title = "About") {
                    ClickablePreference(
                        title = "Version",
                        summary = "1.0.0",
                        onClick = { }
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ListSelectionDialog(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = index == selectedIndex,
                                onClick = { onSelect(index) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = index == selectedIndex,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun GalleryPickerDialog(
    availableApps: List<GalleryAppInfo>,
    onSelectApp: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Gallery App") },
        text = {
            if (availableApps.isEmpty()) {
                Text("No gallery apps found on this device")
            } else {
                Column {
                    availableApps.forEach { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = false,
                                    onClick = { onSelectApp(app.packageName) },
                                    role = Role.Button
                                )
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(app.appName)
                        }
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    TsukiViewerTheme {
        // Preview with minimal composable since SettingsScreen requires ViewModel
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text("Settings Screen Preview", style = MaterialTheme.typography.headlineSmall)
        }
    }
}
