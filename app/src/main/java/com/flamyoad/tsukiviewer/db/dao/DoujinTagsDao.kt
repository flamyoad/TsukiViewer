package com.flamyoad.tsukiviewer.db.dao

import androidx.room.*
import com.flamyoad.tsukiviewer.model.DoujinTag
import com.flamyoad.tsukiviewer.model.Tag

@Dao
interface DoujinTagsDao {

    @Insert
    fun insert(doujinTag: DoujinTag)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(doujinTags: List<DoujinTag>)

    @Query("DELETE FROM doujin_tags WHERE doujinId = :doujinId")
    suspend fun deleteFromDoujin(doujinId: Long)

    @Query("DELETE FROM doujin_tags WHERE doujinId = :tagId")
    suspend fun deleteFromTag(tagId: Long)

    @Query("DELETE FROM doujin_tags")
    suspend fun deleteAll()

    @Query("""
        UPDATE tags
        SET count = count - 1
        WHERE tagId IN (SELECT tagId FROM doujin_tags WHERE doujinId = :doujinId)
    """)
    suspend fun decrementTagCount(doujinId: Long)
}