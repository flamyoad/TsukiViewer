package com.flamyoad.tsukiviewer.ui.settings.folderpicker

import java.io.File

interface FolderPickerListener {
    fun onFolderPick(dir: File)
}