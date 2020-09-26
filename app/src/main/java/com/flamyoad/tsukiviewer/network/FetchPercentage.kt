package com.flamyoad.tsukiviewer.network

data class FetchPercentage(
    val fetched: Int,
    val total: Int
) {
    fun getPercent(): Int {
        return fetched / total
    }

    fun getPercentString(): String {
        return getPercent().toString() + "%" // 50%
    }

    fun getProgress(): String {
        return "$fetched/$total"
    }
}