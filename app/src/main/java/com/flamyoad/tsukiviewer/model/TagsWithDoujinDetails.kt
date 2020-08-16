package com.flamyoad.tsukiviewer.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TagsWithDoujinDetails(
    @Embedded
    val tag: Tag,

    @Relation(
        parentColumn = "tagId",
        entity = DoujinDetails::class,
        entityColumn = "id",
        associateBy = Junction(
            value = DoujinTag::class,
            parentColumn = "tagId",
            entityColumn = "doujinId")
    )
    val doujinDetails: List<DoujinDetails>
)