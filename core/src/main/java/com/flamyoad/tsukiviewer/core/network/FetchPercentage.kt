package com.flamyoad.tsukiviewer.core.network

data class FetchPercentage(
    val fetched: Int,
    val total: Int
) {
    fun getPercent(): Int {
        // Promotes first argument to floating point. If not, it can only return 0
        // e.g. 33/100 will result in 0
        val percent = (fetched.toDouble() / total) * 100

        // Rounds up the float value to its nearest integer
        return percent.toInt()
    }

    fun getPercentString(): String {
        return getPercent().toString() + "%" // 50%
    }

    fun getProgress(): String {
        return "$fetched/$total"
    }
}