package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.db.typeconverter.FolderConverter
import com.flamyoad.tsukiviewer.model.CollectionCriteria
import com.flamyoad.tsukiviewer.model.Tag
import java.io.File

@Dao
@TypeConverters(FolderConverter::class)
interface CollectionCriteriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(criteria: CollectionCriteria)

    @Update
    suspend fun update(criteria: CollectionCriteria)

    @Query("DELETE FROM collection_criteria WHERE collectionId = :collectionId")
    suspend fun delete(collectionId: Long)

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

    @Query("""
        SELECT * FROM tags
        WHERE tagId IN (SELECT value 
                       FROM COLLECTION_CRITERIA 
                       WHERE collectionId = :collectionId AND type = 'excluded_tags') 
    """)
    fun getExcludedTags(collectionId: Long): LiveData<List<Tag>>

    @Query("""
        SELECT value FROM collection_criteria 
        WHERE collectionId = :collectionId AND type = 'directory'
    """)
    fun getDirectories(collectionId: Long): LiveData<List<File>>

    @Query("""
        SELECT value FROM collection_criteria
        WHERE collectionId = :collectionId AND type = 'title' 
    """)
    suspend fun getTitlesBlocking(collectionId: Long): List<String>

    @Query("""
        SELECT * FROM tags
        WHERE tagId IN (SELECT value 
                       FROM COLLECTION_CRITERIA 
                       WHERE collectionId = :collectionId AND type = 'included_tags') 
    """)
    suspend fun getIncludedTagsBlocking(collectionId: Long): List<Tag>

    @Query("""
        SELECT * FROM tags
        WHERE tagId IN (SELECT value 
                       FROM COLLECTION_CRITERIA 
                       WHERE collectionId = :collectionId AND type = 'excluded_tags') 
    """)
    suspend fun getExcludedTagsBlocking(collectionId: Long): List<Tag>

    @Query("""
        SELECT value FROM collection_criteria 
        WHERE collectionId = :collectionId AND type = 'directory'
    """)
    suspend fun getDirectoriesBlocking(collectionId: Long): List<File>
}