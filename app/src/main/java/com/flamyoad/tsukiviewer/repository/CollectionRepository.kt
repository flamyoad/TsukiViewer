package com.flamyoad.tsukiviewer.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.CollectionCriteriaDao
import com.flamyoad.tsukiviewer.db.dao.CollectionDao
import com.flamyoad.tsukiviewer.db.dao.CollectionDoujinDao
import com.flamyoad.tsukiviewer.model.*
import com.flamyoad.tsukiviewer.model.Collection
import java.io.File

class CollectionRepository(context: Context) {
    private val db: AppDatabase = AppDatabase.getInstance(context)

    private val collectionDao: CollectionDao
    private val criteriaDao: CollectionCriteriaDao
    private val collectionDoujinDao: CollectionDoujinDao

    init {
        collectionDao = db.collectionDao()
        criteriaDao = db.collectionCriteriaDao()
        collectionDoujinDao = db.collectionDoujinDao()
    }

    suspend fun get(id: Long): Collection {
        return collectionDao.getBlocking(id)
    }

    suspend fun getAll(): List<Collection> {
        return collectionDao.getAllBlocking()
    }

    fun getAllWithCriterias(): LiveData<List<CollectionWithCriterias>> {
        return collectionDao.getAllWithCriterias()
    }

    fun getAllWithCriterias(keyword: String): LiveData<List<CollectionWithCriterias>> {
        return collectionDao.getAllWithCriterias(keyword)
    }

    suspend fun insert(collection: Collection, criterias: List<CollectionCriteria>) {
        db.withTransaction {
            val collectionId = collectionDao.insert(collection)
            if (collectionId == -1L) {
                return@withTransaction
            }

            // Fill in criterias with collectionId, which is impossible to get before first inserting the collection
            val completedCriterias = criterias.map { criteria ->
                CollectionCriteria(
                    id = null,
                    collectionId = collectionId,
                    type = criteria.type,
                    value = criteria.value,
                    valueName = criteria.valueName
                )
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

    suspend fun updateThumbnail(collectionId: Long?, file: File) {
        if (collectionId == null) return
        collectionDao.updateThumbnail(collectionId, file)
    }

    suspend fun getTitles(id: Long): List<String> {
        return criteriaDao.getTitlesBlocking(id)
    }

    suspend fun getIncludedTags(id: Long): List<Tag> {
        return criteriaDao.getIncludedTagsBlocking(id)
    }

    suspend fun getExcludedTags(id: Long): List<Tag> {
        return criteriaDao.getExcludedTagsBlocking(id)
    }

    suspend fun getDirectories(id: Long): List<File> {
        return criteriaDao.getDirectoriesBlocking(id)
    }

    suspend fun searchIncludedOrExcludedOr(
        includedTags: List<Tag>,
        excludedTags: List<Tag>
    ): List<DoujinDetails> {
        val includedTagsId = includedTags.map { tag -> tag.tagId ?: -1 }
        val excludedTagsId = excludedTags.map { tag -> tag.tagId ?: -1 }
        return collectionDoujinDao.searchIncludedOrExcludedOr(includedTagsId, excludedTagsId)
    }

    suspend fun searchIncludedOrExcludedAnd(
        includedTags: List<Tag>,
        excludedTags: List<Tag>
    ): List<DoujinDetails> {
        val includedTagsId = includedTags.map { tag -> tag.tagId ?: -1 }
        val excludedTagsId = excludedTags.map { tag -> tag.tagId ?: -1 }
        return collectionDoujinDao.searchIncludedOrExcludedAnd(
            includedTagsId,
            excludedTagsId,
            excludedTags.size
        )
    }

    suspend fun searchIncludedAndExcludedOr(
        includedTags: List<Tag>,
        excludedTags: List<Tag>
    ): List<DoujinDetails> {
        val includedTagsId = includedTags.map { tag -> tag.tagId ?: -1 }
        val excludedTagsId = excludedTags.map { tag -> tag.tagId ?: -1 }
        return collectionDoujinDao.searchIncludedAndExcludedOr(
            includedTagsId,
            excludedTagsId,
            includedTags.size
        )
    }

    suspend fun searchIncludedAndExcludedAnd(
        includedTags: List<Tag>,
        excludedTags: List<Tag>
    ): List<DoujinDetails> {
        val includedTagsId = includedTags.map { tag -> tag.tagId ?: -1 }
        val excludedTagsId = excludedTags.map { tag -> tag.tagId ?: -1 }
        return collectionDoujinDao.searchIncludedAndExcludedAnd(
            includedTagsId,
            excludedTagsId,
            includedTags.size,
            excludedTags.size
        )
    }

}