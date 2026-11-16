package com.flamyoad.tsukiviewer.core.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.flamyoad.tsukiviewer.core.model.RelatedMangaCacheEntry

@Dao
interface RelatedMangaCacheDao {
    @Query("SELECT * FROM related_manga_cache WHERE directoryPath = :path")
    suspend fun find(path: String): RelatedMangaCacheEntry?

    @Upsert
    suspend fun upsert(entry: RelatedMangaCacheEntry)

    @Query("DELETE FROM related_manga_cache WHERE directoryPath = :path AND savedAt = :savedAt AND payload = :payload")
    suspend fun delete(path: String, savedAt: Long, payload: String)

    @Query("DELETE FROM related_manga_cache WHERE libraryKey != :libraryKey OR savedAt < :oldest")
    suspend fun prune(libraryKey: String, oldest: Long)

    @Query("DELETE FROM related_manga_cache")
    suspend fun clear()
}
