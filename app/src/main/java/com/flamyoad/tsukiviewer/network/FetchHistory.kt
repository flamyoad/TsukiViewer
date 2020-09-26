package com.flamyoad.tsukiviewer.network

import java.io.File

data class FetchHistory(
    val dir: File,
    val doujinName: String,
    val status: FetchStatus
)