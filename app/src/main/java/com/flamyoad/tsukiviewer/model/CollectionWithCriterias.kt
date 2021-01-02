package com.flamyoad.tsukiviewer.model

import androidx.room.Embedded
import androidx.room.Relation

data class CollectionWithCriterias(
    @Embedded
    val collection: Collection,

    @Relation(parentColumn = "id", entityColumn = "collectionId")
    val criteriaList: List<CollectionCriteria>
) {
    fun getCriteriaNames(): String {
        return criteriaList.joinToString(", ") { item ->
            return@joinToString when (item.type) {
                CollectionCriteria.TITLE -> "+${item.valueName}"
                CollectionCriteria.INCLUDED_TAGS -> "+${item.valueName}"
                CollectionCriteria.EXCLUDED_TAGS -> "-${item.valueName}"
                else -> "+${item.valueName}"
            }
        }
    }
}