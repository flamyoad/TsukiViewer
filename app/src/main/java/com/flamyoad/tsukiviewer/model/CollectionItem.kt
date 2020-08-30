package com.flamyoad.tsukiviewer.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
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

    val collectionName: String
)