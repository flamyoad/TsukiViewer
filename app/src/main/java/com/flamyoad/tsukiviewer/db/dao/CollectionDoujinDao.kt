package com.flamyoad.tsukiviewer.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.flamyoad.tsukiviewer.model.DoujinDetails

@Dao
interface CollectionDoujinDao {

    @Query(
        """
        SELECT * FROM doujin_details 
        WHERE id IN (
	        SELECT doujinId FROM doujin_tags
	        WHERE doujin_tags.tagId IN (:includedTagsId) 
	        GROUP BY doujinId
		        EXCEPT 
	        SELECT doujinId FROM doujin_tags
            WHERE doujin_tags.tagId IN (:excludedTagsId)
	        GROUP BY doujin_tags.doujinId)
    """
    )
    // Search with Title + Included Tags (OR) + Excluded Tags (OR)
    suspend fun searchIncludedOrExcludedOr(
        includedTagsId: List<Long>,
        excludedTagsId: List<Long>
    ): List<DoujinDetails>

    @Query(
        """
        SELECT * FROM doujin_details 
        WHERE id IN (
	        SELECT doujinId FROM doujin_tags
	        WHERE doujin_tags.tagId IN (:includedTagsId) 
	        GROUP BY doujinId
		        EXCEPT 
	        SELECT doujinId FROM doujin_tags
	        WHERE doujin_tags.tagId IN (:excludedTagsId)
	        GROUP BY doujinId
	        HAVING COUNT(*) = :excludedTagsCount)  -----> Replace with list size
    """
    )
    // Search with Title + Included Tags (OR) + Excluded Tags (AND)
    suspend fun searchIncludedOrExcludedAnd(
        includedTagsId: List<Long>,
        excludedTagsId: List<Long>,
        excludedTagsCount: Int
    ): List<DoujinDetails>


    @Query(
        """
        SELECT * FROM doujin_details 
        WHERE id IN (
	        SELECT doujinId FROM doujin_tags
	        WHERE doujin_tags.tagId IN (:includedTagsId) 
	        GROUP BY doujinId
		        EXCEPT 
	        SELECT doujinId FROM doujin_tags
	        WHERE doujin_tags.tagId IN (:excludedTagsId)
	        GROUP BY doujinId
	        HAVING COUNT(*) = :includedTagsCount)  -----> Replace with list size
    """
    )
    // Search with Title + Included Tags (AND) + Excluded Tags (OR)
    suspend fun searchIncludedAndExcludedOr(
        includedTagsId: List<Long>,
        excludedTagsId: List<Long>,
        includedTagsCount: Int
    ): List<DoujinDetails>

    @Query(
        """
        SELECT * FROM doujin_details 
        WHERE id IN (
	        SELECT doujinId FROM doujin_tags
	        WHERE doujin_tags.tagId IN (:includedTagsId)  
	        GROUP BY doujinId
	        HAVING COUNT(*) = :includedTagsCount   
		        EXCEPT 
	        SELECT doujinId FROM doujin_tags
	        WHERE doujin_tags.tagId IN (:excludedTagsId)
	        GROUP BY doujinId
	        HAVING COUNT(*) = :excludedTagsCount  
)
    """
    )
    // Search with Title + Included Tags (AND) + Excluded Tags (AND)
    suspend fun searchIncludedAndExcludedAnd(
        includedTagsId: List<Long>,
        excludedTagsId: List<Long>,
        includedTagsCount: Int,
        excludedTagsCount: Int
    ): List<DoujinDetails>
}