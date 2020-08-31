package com.flamyoad.tsukiviewer.model

import androidx.room.*
import com.flamyoad.tsukiviewer.db.typeconverter.FolderConverter
import java.io.File

@Entity(
    tableName = "collection_item",
    foreignKeys = [ForeignKey(
        entity = DoujinCollection::class,
        parentColumns = ["name"],
        childColumns = ["collectionName"],
        onDelete = ForeignKey.CASCADE
    )]
)
@TypeConverters(FolderConverter::class)

data class CollectionItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    val absolutePath: File,

    val collectionName: String,

    @Ignore
    val isHeader: Boolean = false,

    @Ignore
    val doujin: Doujin? = null
) {
    constructor(id: Long, absolutePath: File, collectionName: String) : this(
        id,
        absolutePath,
        collectionName,
        false,
        null
    )
}