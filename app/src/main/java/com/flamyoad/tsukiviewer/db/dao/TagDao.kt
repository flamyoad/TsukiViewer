package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.db.typeconverter.TagSortingModeConverter
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.model.TagSortingMode

@Dao
@TypeConverters(TagSortingModeConverter::class)
interface TagDao {

    @Insert
    suspend fun insert(tag: Tag): Long

    @Delete
    suspend fun delete(tag: Tag)

    @Query("DELETE FROM tags WHERE tagId = :tagId")
    suspend fun delete(tagId: Long)

    @Query("DELETE FROM tags")
    suspend fun deleteAll()

    @Query("SELECT * FROM tags ORDER BY name")
    fun getAll(): LiveData<List<Tag>>

    @Query("""
        SELECT * FROM tags 
        WHERE name LIKE '%' || :keyword || '%'
        ORDER BY name 
        """)
    fun getAllWithFilter(keyword: String): LiveData<List<Tag>>

    /*
        You can't use bind variables (parameters) to reference columns in the ORDER BY clause.
        https://stackoverflow.com/questions/48172807/room-database-full-dynamic-query
    */
    @Query("""
        SELECT * FROM tags 
        WHERE name LIKE '%' || :keyword || '%'
        ORDER BY 
        CASE WHEN :sortMode = 'NAME_ASCENDING' THEN name END,
        CASE WHEN :sortMode = 'NAME_DESCENDING' THEN name END DESC,
        CASE WHEN :sortMode = 'COUNT_ASCENDING' THEN count END,
        CASE WHEN :sortMode = 'COUNT_DESCENDING' THEN count END DESC
        """)
    fun getAllWithFilter(keyword: String, sortMode: TagSortingMode): LiveData<List<Tag>>

    @Query("SELECT * FROM tags WHERE type = :category ORDER BY name")
    fun getByCategory(category: String): LiveData<List<Tag>>

    @Query("""
        SELECT * FROM tags 
        WHERE type = :category AND name LIKE '%' || :keyword || '%'
        ORDER BY 
        CASE WHEN :sortMode = 'NAME_ASCENDING' THEN name END,
        CASE WHEN :sortMode = 'NAME_DESCENDING' THEN name END DESC,
        CASE WHEN :sortMode = 'COUNT_ASCENDING' THEN count END,
        CASE WHEN :sortMode = 'COUNT_DESCENDING' THEN count END DESC
        """)
    fun getByCategoryWithFilter(category: String, keyword: String, sortMode: TagSortingMode): LiveData<List<Tag>>

    @Query("SELECT * FROM tags WHERE type = :type AND name = :name")
    suspend fun get(type: String, name: String): Tag?

    @Query("SELECT tagId from tags WHERE type = :type AND name = :name")
    suspend fun getId(type: String, name: String): Long

    @Query("SELECT EXISTS(SELECT * FROM tags WHERE type = :type AND name = :name)")
    suspend fun exists(type: String, name: String): Boolean

    @Query("UPDATE tags SET count = count + 1 WHERE type = :type AND name = :name")
    suspend fun incrementCount(type: String, name: String)

    @Query("UPDATE tags SET count = count - 1 WHERE type = :type AND name = :name")
    suspend fun decrementCount(type: String, name: String)

}

//    @Query("""
//        SELECT * FROM tags
//        WHERE name LIKE '%' || :keyword || '%'
//        ORDER BY :sortColumn DESC
//        """)
//    fun getAllWithFilterDesc(keyword: String, sortColumn: String): LiveData<List<Tag>>

//    @Query("""
//        SELECT * FROM tags
//        WHERE type = :category AND name LIKE '%' || :keyword || '%'
//        ORDER BY :sortColumn DESC
//        """)
//    fun getByCategoryWithFilterDesc(category: String, keyword: String, sortColumn: String): LiveData<List<Tag>>