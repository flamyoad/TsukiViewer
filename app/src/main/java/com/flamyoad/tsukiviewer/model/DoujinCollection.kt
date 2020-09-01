package com.flamyoad.tsukiviewer.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "doujin_collection")
data class DoujinCollection(
    @PrimaryKey
    val name: String,

    @Ignore // Field for dialog logic
    var isTicked: Boolean
) {
    constructor(name: String) : this(name, false)
}