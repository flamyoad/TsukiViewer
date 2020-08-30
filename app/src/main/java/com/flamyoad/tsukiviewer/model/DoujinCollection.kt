package com.flamyoad.tsukiviewer.model

import androidx.room.Entity
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey

@Entity(tableName = "doujin_collection")
data class DoujinCollection(
    @PrimaryKey(OnConflictStrategy = OnConflictStrategy.IGNORE)
    val name: String
)