package com.flamyoad.tsukiviewer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class Tag (
    @PrimaryKey(autoGenerate = true)
    val tagId: Long? = null,

    val type: String,

    val name: String,

    val url: String = "",

    @ColumnInfo(name = "count")
    val count: Int = 1
)