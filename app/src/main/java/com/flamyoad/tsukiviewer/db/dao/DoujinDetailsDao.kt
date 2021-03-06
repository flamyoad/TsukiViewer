package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.db.typeconverter.FolderConverter
import com.flamyoad.tsukiviewer.model.DoujinDetails
import com.flamyoad.tsukiviewer.model.DoujinDetailsWithTags
import com.flamyoad.tsukiviewer.model.ShortTitle

// TODO: Refactor this class to two - DoujinDetailsDao and DoujinLongDetailsDao

@Dao
@TypeConverters(FolderConverter::class)
interface DoujinDetailsDao {

    // Compile time error will occur if we change the return type to int instead of long
    // Looks like Android's room defaults to long when it comes to primary keys
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(doujinDetails: DoujinDetails): Long

    @Update
    suspend fun update(doujinDetails: DoujinDetails)

    @Delete
    suspend fun delete(doujinDetails: DoujinDetails)

    @Query("DELETE FROM doujin_details")
    suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT * FROM doujin_details WHERE fullTitleEnglish = :fullTitleEnglish)")
    suspend fun existsByTitle(fullTitleEnglish: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM doujin_details WHERE absolutePath = :absolutePath)")
    suspend fun existsByAbsolutePath(absolutePath: String): Boolean

    @Query("SELECT shortTitleEnglish, absolutePath FROM doujin_details")
    suspend fun getAllShortTitles(): List<ShortTitle>

    @Query("""
        SELECT * FROM doujin_details 
        WHERE fullTitleEnglish LIKE '%' || :query || '%' OR 
        fullTitleJapanese LIKE '%' || :query || '%'
        """)
    suspend fun findByTitle(query: String): List<DoujinDetails>

    @Query("SELECT * FROM doujin_details WHERE absolutePath = :absolutePath")
    suspend fun findByAbsolutePath(absolutePath: String): List<DoujinDetails>

    @Query("SELECT shortTitleEnglish FROM doujin_details WHERE absolutePath = :absolutePath")
    fun findShortTitleByPath(absolutePath: String): List<String>

    //    SELECT * FROM doujin_tags as dt
    //    INNER JOIN doujin_details ON doujin_details.id = dt.doujinId
    //    INNER JOIN tags ON tags.tagId = dt.tagId
    //    WHERE name IN ('chinese', 'translated', 'dilf')
    //    GROUP BY doujinId
    //    HAVING COUNT(doujinId) = 3

    // This method searches for doujins that have all the included tags (No more, no less)
    @Query("""
        SELECT * FROM doujin_tags as dt
        INNER JOIN doujin_details ON doujin_details.id = dt.doujinId
        INNER JOIN tags ON tags.tagId = dt.tagId
        WHERE name IN (:tags)
        GROUP BY doujinId
        HAVING COUNT(doujinId) = :tagCount
    """)
    suspend fun findByTags(tags: List<String>, tagCount: Int): List<DoujinDetails>

    // This method searches for doujins that have at least 1 of the given tags
    @Query("""
        SELECT * FROM doujin_tags as dt
        INNER JOIN doujin_details ON doujin_details.id = dt.doujinId
        INNER JOIN tags ON tags.tagId = dt.tagId
        WHERE name IN (:tags)
        GROUP BY doujinId
        HAVING COUNT(doujinId) = 1
    """)
    suspend fun findByTags(tags: List<String>): List<DoujinDetails>
    
    @Query("SELECT * FROM doujin_details")
    suspend fun getAllShortDetails(): List<DoujinDetails>

    @Transaction
    @Query("SELECT * FROM doujin_details")
    suspend fun getAllLongDetails(): List<DoujinDetailsWithTags>

    @Transaction
    @Query("SELECT * FROM doujin_details WHERE fullTitleEnglish = :fullTitle")
    fun getLongDetailsByFullTitle(fullTitle: String): LiveData<DoujinDetailsWithTags>

    @Transaction
    @Query("SELECT * FROM doujin_details WHERE absolutePath = :absolutePath")
    fun getLongDetailsByPath(absolutePath: String): LiveData<DoujinDetailsWithTags>

    @Transaction
    @Query("SELECT * FROM doujin_details WHERE absolutePath = :absolutePath")
    suspend fun getLongDetailsByPathBlocking(absolutePath: String): DoujinDetailsWithTags?

}