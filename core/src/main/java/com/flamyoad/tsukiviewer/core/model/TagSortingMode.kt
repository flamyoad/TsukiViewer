package com.flamyoad.tsukiviewer.core.model

enum class TagSortingMode(private val description: String) {
    NAME_ASCENDING("By name ascending"),
    NAME_DESCENDING("By name descending"),
    COUNT_ASCENDING("By count ascending"),
    COUNT_DESCENDING("By count descending");

    fun getDescription(): String {
        return description
    }

    companion object {
        private val modeByDescription =
            TagSortingMode.values().associateBy(TagSortingMode::description)

        fun fromDescription(desc: String): TagSortingMode {
            return modeByDescription[desc] ?: NAME_ASCENDING
        }
    }
}