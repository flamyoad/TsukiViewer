package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.model.CollectionCriteria
import com.flamyoad.tsukiviewer.model.Tag

@Dao
interface CollectionCriteriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(criteria: CollectionCriteria)

    @Update
    suspend fun update(criteria: CollectionCriteria)

    @Query("DELETE FROM collection_criteria WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("""
        SELECT value FROM collection_criteria
        WHERE collectionId = :collectionId AND type = 'title' 
    """)
    fun getTitles(collectionId: Long): LiveData<List<String>>

    @Query("""
        SELECT * FROM tags
        WHERE tagId IN (SELECT value 
                       FROM COLLECTION_CRITERIA 
                       WHERE collectionId = :collectionId AND type = 'included_tags') 
    """)
    fun getIncludedTags(collectionId: Long): LiveData<List<Tag>>

}