package com.flamyoad.tsukiviewer.ui.screens.tags

/**
 * UI model for a tag.
 */
data class TagUiModel(
    val id: Long,
    val name: String,
    val type: TagType,
    val count: Int = 0,
    val url: String = ""
)

/**
 * Tag types matching the core module's TagType.
 */
enum class TagType(val displayName: String, val shortName: String) {
    ALL("All", "all"),
    PARODIES("Parodies", "parody"),
    CHARACTERS("Characters", "character"),
    TAGS("Tags", "tag"),
    ARTISTS("Artists", "artist"),
    GROUPS("Groups", "group"),
    LANGUAGES("Languages", "language"),
    CATEGORIES("Categories", "category");
    
    companion object {
        fun fromShortName(shortName: String): TagType {
            return entries.find { it.shortName == shortName } ?: ALL
        }
        
        fun fromString(type: String): TagType {
            return entries.find { 
                it.shortName.equals(type, ignoreCase = true) || 
                it.displayName.equals(type, ignoreCase = true) 
            } ?: ALL
        }
    }
}

/**
 * Sorting mode for tags.
 */
enum class TagSortingMode(val displayName: String) {
    NAME_ASCENDING("Name (A-Z)"),
    NAME_DESCENDING("Name (Z-A)"),
    COUNT_ASCENDING("Count (Low to High)"),
    COUNT_DESCENDING("Count (High to Low)")
}

/**
 * UI state for the Tags screen.
 */
data class TagsUiState(
    val allTags: List<TagUiModel> = emptyList(),
    val filteredTags: List<TagUiModel> = emptyList(),
    val selectedTabIndex: Int = 0,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val sortingMode: TagSortingMode = TagSortingMode.NAME_ASCENDING,
    val isLoading: Boolean = false,
    val snackbarMessage: String? = null
) {
    val tabTypes: List<TagType> = listOf(
        TagType.ALL,
        TagType.PARODIES,
        TagType.CHARACTERS,
        TagType.TAGS,
        TagType.ARTISTS,
        TagType.GROUPS,
        TagType.LANGUAGES,
        TagType.CATEGORIES
    )
    
    val currentTagType: TagType
        get() = tabTypes.getOrElse(selectedTabIndex) { TagType.ALL }
}
