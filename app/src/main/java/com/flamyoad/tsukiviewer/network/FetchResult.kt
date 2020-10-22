package com.flamyoad.tsukiviewer.network

data class FetchResult(
    val metadata: Metadata? = null,
    val status: FetchStatus
) {
    fun getDoujinTitle(): String? {
        return metadata?.result?.first()?.title?.english
    }
}
