package com.flamyoad.tsukiviewer.model

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "doujin_tags",
    primaryKeys = ["doujinId", "tagId"],
    indices = arrayOf(Index(value = ["doujinId", "tagId"]))
)

data class DoujinTag (
    val doujinId: Long,
    val tagId: Long
)