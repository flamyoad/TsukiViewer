package com.flamyoad.tsukiviewer.repository

import android.content.Context
import androidx.room.withTransaction
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.CollectionCriteriaDao
import com.flamyoad.tsukiviewer.db.dao.CollectionDao
import com.flamyoad.tsukiviewer.model.Collection
import com.flamyoad.tsukiviewer.model.CollectionCriteria

class CollectionRepository(private val context: Context) {
    private val db: AppDatabase

    val collectionDao: CollectionDao
    val criteriaDao: CollectionCriteriaDao

    init {
        db = AppDatabase.getInstance(context)

        collectionDao = db.collectionDao()
        criteriaDao = db.collectionCriteriaDao()
    }

    suspend fun insert(collection: Collection, criterias: List<CollectionCriteria>) {
        db.withTransaction {
            val collectionId = collectionDao.insert(collection)
            if (collectionId == -1L) {
                return@withTransaction
            }

            // Fill in criterias with collectionId, which is impossible to get before first inserting the collection
            val completedCriterias = criterias.map { c ->
                CollectionCriteria(
                    id = null,
                    collectionId = collectionId,
                    type = c.type,
                    value = c.value)
            }

            for (criteria in completedCriterias) {
                criteriaDao.insert(criteria)
            }
        }
    }

    suspend fun delete(collectionId: Long) {
        db.withTransaction {
            collectionDao.delete(collectionId)
            criteriaDao.delete(collectionId)
        }
    }

    // It's ok to use INSERT for new/old because onConflictStrategy is REPLACE
    suspend fun update(criterias: List<CollectionCriteria>) {
        db.withTransaction {
            for (criteria in criterias) {
                criteriaDao.insert(criteria)
            }
        }
    }

}