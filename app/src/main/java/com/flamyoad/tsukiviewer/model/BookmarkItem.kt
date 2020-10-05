package com.flamyoad.tsukiviewer.model

import androidx.room.*
import com.flamyoad.tsukiviewer.db.typeconverter.FolderConverter
import java.io.File

@Entity(
    tableName = "bookmark_item",
    foreignKeys = [ForeignKey(
        entity = BookmarkGroup::class,
        parentColumns = ["name"],
        childColumns = ["parentName"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
@TypeConverters(FolderConverter::class)

data class BookmarkItem(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val absolutePath: File,
    val parentName: String,
    val dateAdded: Long,
    @Ignore val doujin: Doujin? = null
) {

    constructor(id: Long, absolutePath: File, parentName: String, dateAdded: Long) : this(
        id,
        absolutePath,
        parentName,
        dateAdded,
        null
    )

    @Ignore var isSelected: Boolean = false
}