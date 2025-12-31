package com.flamyoad.tsukiviewer.core.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class DoujinDetailsWithTags(
    @Embedded
    val doujinDetails: DoujinDetails,

    @Relation(
        parentColumn = "id",
        entity = Tag::class,
        entityColumn = "tagId",
        associateBy = Junction(
            value = DoujinTag::class,
            parentColumn = "doujinId",
            entityColumn = "tagId")
    )
    val tags: List<Tag>
)