package com.flamyoad.tsukiviewer.ui.screens.settings

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.MenuBook
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.flamyoad.tsukiviewer.core.R as CoreR
import com.flamyoad.tsukiviewer.core.prefs.PreferenceKeys
import com.flamyoad.tsukiviewer.ui.R
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flamyoad.tsukiviewer.ui.screens.settings.components.ClickablePreference
import com.flamyoad.tsukiviewer.ui.screens.settings.components.SettingsCategory
import com.flamyoad.tsukiviewer.ui.screens.settings.components.SwitchPreference
import com.flamyoad.tsukiviewer.ui.theme.TsukiViewerTheme
import com.flamyoad.tsukiviewer.ui.screens.doujin.GridViewStyle
import com.flamyoad.tsukiviewer.ui.screens.doujin.ReaderMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToIncludedFolders: () -> Unit = {},
    onRestartApp: () -> Unit = {},
    modifier: Modifier = Modifier,
    appLockEnabled: Boolean = false,
    onAppLockChange: ((Boolean) -> Unit)? = null,
    appLockTimeoutLabel: String? = null,
    onAppLockTimeoutClick: (() -> Unit)? = null,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current.applicationContext as android.app.Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val appVersion = remember(context) { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
    var showAbout by rememberSaveable { mutableStateOf(false) }
    var showClearData by rememberSaveable { mutableStateOf(false) }
    var confirmClear by rememberSaveable { mutableStateOf<ClearDataOption?>(null) }
    var showVolumeSettings by rememberSaveable { mutableStateOf(false) }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            title = { Text("About") },
            text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Version ${appVersion.orEmpty()}")
                Text("Created by Flamyoad")
                TextButton(onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://steamcommunity.com/id/flamyoad/"))
                    runCatching { context.startActivity(intent) }
                }) { Text("Steam Profile") }
            } },
            confirmButton = { TextButton(onClick = { showAbout = false }) { Text("OK") } }
        )
    }
    if (showClearData) {
        AlertDialog(
            onDismissRequest = { showClearData = false },
            title = { Text("Clear data") },
            text = { Column {
                ClearDataOption.entries.forEach { option ->
                    ClickablePreference(option.title, onClick = {
                        showClearData = false
                        confirmClear = option
                    })
                }
            } },
            confirmButton = { TextButton(onClick = { showClearData = false }) { Text("Cancel") } }
        )
    }
    confirmClear?.let { option ->
        AlertDialog(
            onDismissRequest = { confirmClear = null },
            title = { Text(option.title) },
            text = { Text("This action cannot be undone") },
            confirmButton = { TextButton(onClick = {
                confirmClear = null
                viewModel.clearData(option)
            }) { Text("Yes") } },
            dismissButton = { TextButton(onClick = { confirmClear = null }) { Text("No") } }
        )
    }
    if (showVolumeSettings) {
        VolumeSettingsDialog(viewModel, onDismiss = { showVolumeSettings = false })
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    
    var showReaderModeDialog by rememberSaveable { mutableStateOf(false) }
    var showGridStyleDialog by rememberSaveable { mutableStateOf(false) }
    var showViewModeDialog by rememberSaveable { mutableStateOf(false) }
    
    var showLandingScreenDialog by rememberSaveable { mutableStateOf(false) }

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
            title = "Default style for image grid",
            options = GridViewStyle.entries.map { it.name },
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
    
    if (showLandingScreenDialog) {
        ListSelectionDialog(
            title = "Landing screen",
            options = listOf("Doujin Details", "Image Grid"),
            selectedIndex = if (uiState.openImageGridFirst) 1 else 0,
            onSelect = { index ->
                viewModel.setLandingScreen(index == 1)
                showLandingScreenDialog = false
            },
            onDismiss = { showLandingScreenDialog = false }
        )
    }

    // Gallery picker dialog
    if (uiState.showGalleryPickerDialog) {
        GalleryPickerDialog(
            availableApps = remember(viewModel) { viewModel.getImageViewerApps() },
            onSelectApp = { packageName ->
                viewModel.setExternalGalleryPackage(packageName)
                viewModel.hideGalleryPicker()
            },
            onDismiss = viewModel::hideGalleryPicker
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                expandedHeight = 56.dp,
                title = { Text("Settings", fontWeight = FontWeight.Medium, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
        if (uiState.isLoading || uiState.clearingData) {
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
                SettingsCategory(title = "General", icon = Icons.Default.Tune) {
                    if (onAppLockChange != null) {
                        SwitchPreference(
                            title = stringResource(R.string.app_lock),
                            summary = stringResource(R.string.app_lock_summary),
                            checked = appLockEnabled,
                            onCheckedChange = onAppLockChange
                        )
                    }
                    if (onAppLockTimeoutClick != null) {
                        ClickablePreference(
                            title = stringResource(R.string.app_lock_timeout),
                            summary = appLockTimeoutLabel,
                            onClick = onAppLockTimeoutClick
                        )
                    }
                    ClickablePreference(
                        title = "Included folders",
                        summary = "Select folders to scan",
                        onClick = onNavigateToIncludedFolders
                    )
                    ClickablePreference(
                        title = "Default style for image grid",
                        summary = uiState.defaultGridViewStyle.name,
                        onClick = { showGridStyleDialog = true }
                    )
                    ClickablePreference(
                        title = "Landing screen",
                        summary = if (uiState.openImageGridFirst) "Image Grid" else "Doujin Details",
                        onClick = { showLandingScreenDialog = true }
                    )
                    SwitchPreference(
                        title = "Use Windows Explorer sort pattern in image list",
                        summary = "Reverts to length-based natural sort if disabled. Should not be disabled in most cases.",
                        checked = uiState.useWindowsExplorerSort,
                        onCheckedChange = viewModel::setUseWindowsExplorerSort
                    )
                    SwitchPreference(
                        title = "Show confirmation dialog before quitting",
                        checked = uiState.confirmBeforeQuit,
                        onCheckedChange = viewModel::setConfirmBeforeQuit
                    )
                    SwitchPreference(
                        title = stringResource(CoreR.string.show_related_manga),
                        summary = stringResource(CoreR.string.show_related_manga_summary),
                        checked = uiState.showRelatedManga,
                        onCheckedChange = viewModel::setShowRelatedManga
                    )
                    SwitchPreference(
                        title = "Use new Compose UI",
                        summary = "Switch between modern Compose UI and classic XML views. App will restart when changed.",
                        checked = uiState.useComposeUi,
                        onCheckedChange = {
                            viewModel.setUseComposeUi(it)
                            onRestartApp()
                        }
                    )
                }

                HorizontalDivider()
                SettingsCategory(title = "Reader", icon = Icons.Default.MenuBook) {
                    SwitchPreference(
                        title = "View using external application",
                        summary = "Turn this on if you wish to read using external image viewers",
                        checked = uiState.useExternalGallery,
                        onCheckedChange = viewModel::setUseExternalGallery
                    )
                    ClickablePreference(
                        title = "Choose external image viewer",
                        summary = uiState.externalGalleryPackage.ifEmpty { null },
                        onClick = viewModel::showGalleryPicker,
                        enabled = uiState.useExternalGallery
                    )
                    ClickablePreference(
                        title = "Default reader mode",
                        summary = uiState.defaultReaderMode.displayName,
                        onClick = { showReaderModeDialog = true }
                    )
                    SwitchPreference(
                        title = "Automatic two-page spreads",
                        summary = "Show two pages on wide screens in landscape",
                        checked = uiState.automaticSpreads,
                        onCheckedChange = viewModel::setAutomaticSpreads
                    )
                    SwitchPreference(
                        title = "Separate cover",
                        checked = uiState.separateCover,
                        onCheckedChange = viewModel::setSeparateCover
                    )
                    SwitchPreference(
                        title = "Right-to-left reading",
                        summary = "Put the first page on the right and reverse horizontal swipes",
                        checked = uiState.readerRightToLeft,
                        onCheckedChange = viewModel::setReaderRightToLeft
                    )
                    SwitchPreference(
                        title = "Autoplay GIFs",
                        summary = "Play animated GIFs automatically in the reader",
                        checked = uiState.readerGifAutoplay,
                        onCheckedChange = viewModel::setReaderGifAutoplay
                    )
                    SwitchPreference(
                        title = "Scroll using volume buttons",
                        checked = uiState.scrollByVolumeButtons,
                        onCheckedChange = viewModel::setScrollByVolumeButtons
                    )
                    ClickablePreference("Volume button settings", onClick = { showVolumeSettings = true })
                }

                HorizontalDivider()
                ClickablePreference(
                    title = "Doujin View Mode",
                    summary = uiState.doujinViewMode.displayName,
                    onClick = { showViewModeDialog = true }
                )
                ClickablePreference("Clear data", onClick = { showClearData = true })
                HorizontalDivider()
                ClickablePreference("About", onClick = { showAbout = true })

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

private data class VolumeSetting(
    val key: String,
    val title: String,
    val values: List<String>,
    val labels: List<String>,
    val default: String = values.first()
)

private val volumeActions = listOf("GoToPrevPage", "GoToNextPage", "Nothing")
private val volumeActionLabels = listOf("Go to previous page", "Go to next page", "Do nothing")
private val volumeDistances = listOf("250", "500", "750", "1000")
private val volumeSettings = listOf(
    VolumeSetting(PreferenceKeys.VOLUME_UP_ACTION, "Volume up button action", volumeActions, volumeActionLabels),
    VolumeSetting(PreferenceKeys.VOLUME_DOWN_ACTION, "Volume down button action", volumeActions, volumeActionLabels, "GoToNextPage"),
    VolumeSetting(PreferenceKeys.SCROLL_MODE, "Scrolling method", listOf("PageByPage", "FixedDistance"),
        listOf("Scroll by page", "Scroll by fixed distance")),
    VolumeSetting(PreferenceKeys.SCROLLING_DISTANCE, "Scrolling distance on each key press",
        volumeDistances, volumeDistances.map { "$it pixels" })
)

@Composable
private fun VolumeSettingsDialog(viewModel: SettingsViewModel, onDismiss: () -> Unit) {
    var selectedKey by rememberSaveable { mutableStateOf<String?>(null) }
    var revision by remember { mutableStateOf(0) }
    val values = remember(viewModel, revision) {
        volumeSettings.associate { it.key to viewModel.getPreference(it.key, it.default) }
    }
    val selected = volumeSettings.firstOrNull { it.key == selectedKey }
    if (selected != null) {
        ListSelectionDialog(
            title = selected.title,
            options = selected.labels,
            selectedIndex = selected.values.indexOf(values[selected.key]),
            onSelect = { index ->
                viewModel.setPreference(selected.key, selected.values[index])
                revision++
                selectedKey = null
            },
            onDismiss = { selectedKey = null }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Volume button settings") },
            text = { Column {
                volumeSettings.forEach { setting ->
                    ClickablePreference(
                        title = setting.title,
                        summary = setting.labels.getOrNull(setting.values.indexOf(values[setting.key])),
                        onClick = { selectedKey = setting.key }
                    )
                }
            } },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Done") } }
        )
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
