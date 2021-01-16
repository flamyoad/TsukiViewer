package com.flamyoad.tsukiviewer.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark_group")
data class BookmarkGroup(
    @PrimaryKey val name: String,

    @Ignore var pic: Uri,
    @Ignore var totalItems: Int,
    @Ignore var lastDate: Long,
    @Ignore var isTicked: Boolean
) {

    constructor(name: String) : this(
        name,
        pic = Uri.EMPTY,
        totalItems = 0,
        lastDate = 0,
        isTicked = false
    )

    override fun equals(other: Any?): Boolean {
        return this.name === (other as BookmarkGroup).name
    }

    override fun hashCode(): Int {
        return this.name.hashCode()
    }
}