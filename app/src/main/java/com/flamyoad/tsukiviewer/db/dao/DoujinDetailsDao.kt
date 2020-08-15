package com.flamyoad.tsukiviewer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.model.DoujinDetails
import com.flamyoad.tsukiviewer.model.DoujinDetailsWithTags
import java.io.File

// TODO: Refactor this class to two - DoujinDetailsDao and DoujinLongDetailsDao

@Dao
interface DoujinDetailsDao {

    // Compile time error will occur if we change the return type to int instead of long
    // Looks like Android's room defaults to long when it comes to primary keys
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(doujinDetails: DoujinDetails): Long

    @Update
    suspend fun update(doujinDetails: DoujinDetails)

    @Query("SELECT EXISTS(SELECT * FROM doujin_details WHERE fullTitleEnglish = :fullTitleEnglish)")
    suspend fun existsByTitle(fullTitleEnglish: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM doujin_details WHERE absolutePath = :absolutePath)")
    suspend fun existsByAbsolutePath(absolutePath: String): Boolean

    @Query("SELECT * FROM doujin_details WHERE fullTitleEnglish LIKE '%' || :query || '%' OR fullTitleJapanese LIKE '%' || :query || '%'")
    suspend fun findByTitle(query: String): List<DoujinDetails>

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
}