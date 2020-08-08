package com.flamyoad.tsukiviewer.ui.settings.folderpicker

import java.io.File

interface ParentDirectoryListener {
    fun onParentDirectoryClick(dir: File)
}