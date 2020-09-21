package com.flamyoad.tsukiviewer.model

enum class TagSortingMode(private val description: String) {
    NAME_ASCENDING("By name ascending"),
    NAME_DESCENDING("By name descending"),
    COUNT_ASCENDING("By count ascending"),
    COUNT_DESCENDING("By count descending");

    fun getDescription() = description
}