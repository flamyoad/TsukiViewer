package com.flamyoad.tsukiviewer.core.network

data class FetchResult(
    val metadata: Metadata? = null,
    val status: FetchStatus
) {
    fun getDoujinTitle(): String? {
        return metadata?.result?.first()?.title?.english
    }
}
