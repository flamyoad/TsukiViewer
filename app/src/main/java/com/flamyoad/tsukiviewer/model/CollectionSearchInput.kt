package com.flamyoad.tsukiviewer.model

import java.io.File

data class CollectionSearchInput(
    val collection: Collection,
    val titleKeywords: List<String>,
    val includedTags: List<Tag>,
    val excludedTags: List<Tag>,
    val minNumberPages: Int = Int.MIN_VALUE,
    val maxNumberPages: Int = Int.MAX_VALUE,
    val mustHaveDirs: List<File>
)