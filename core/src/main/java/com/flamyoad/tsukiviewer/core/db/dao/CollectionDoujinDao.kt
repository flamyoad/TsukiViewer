package com.flamyoad.tsukiviewer.core.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.flamyoad.tsukiviewer.core.model.DoujinDetails

@Dao
interface CollectionDoujinDao {

    // Search with Included Tags (OR)
    @Query("""
        SELECT * FROM doujin_details as details
        INNER JOIN doujin_tags ON details.id = doujin_tags.doujinId
        WHERE tagId IN (:includedTagsId) 
        GROUP BY details.id
    """)
    suspend fun searchIncludedOr(includedTagsId: List<Long>): List<DoujinDetails>


    // Search with Included Tags (AND)
    @Query("""
        SELECT * FROM doujin_tags
        INNER JOIN doujin_details ON doujin_tags.doujinId = doujin_details.id
        WHERE doujin_tags.tagId IN (:includedTagsId)  ----> Replace with list args
        GROUP BY doujin_tags.doujinId
        HAVING COUNT(*) = :includedTagsCount
    """)
    suspend fun searchIncludedAnd(includedTagsId: List<Long>, includedTagsCount: Int): List<DoujinDetails>


    // Search with Excluded Tags (OR)
    @Query("""
        SELECT * FROM doujin_details WHERE id IN (
	        SELECT doujinId FROM doujin_tags
		        EXCEPT
	        SELECT doujinId FROM doujin_tags
	        WHERE doujin_tags.tagId IN (:excludedTagsId)
	        GROUP BY doujin_tags.doujinId
        ) 
    """)
    suspend fun searchExcludedOr(excludedTagsId: List<Long>): List<DoujinDetails>

    // Search with Excluded Tags (AND)
    @Query("""
        SELECT * FROM doujin_details WHERE id IN (
	        SELECT doujinId FROM doujin_tags
		        EXCEPT
	        SELECT doujinId FROM doujin_tags
	        INNER JOIN doujin_details ON doujin_tags.doujinId = doujin_details.id
	        WHERE doujin_tags.tagId IN (:excludedTagsId) 
	        GROUP BY doujin_tags.doujinId
	        HAVING COUNT(*) = :excludedTagsCount
) 
    """)
    suspend fun searchExcludedAnd(excludedTagsId: List<Long>, excludedTagsCount: Int): List<DoujinDetails>


    // Search with Included Tags (OR) + Excluded Tags (OR)
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
    suspend fun searchIncludedOrExcludedOr(
        includedTagsId: List<Long>,
        excludedTagsId: List<Long>
    ): List<DoujinDetails>


    // Search with Included Tags (OR) + Excluded Tags (AND)
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
    suspend fun searchIncludedOrExcludedAnd(
        includedTagsId: List<Long>,
        excludedTagsId: List<Long>,
        excludedTagsCount: Int
    ): List<DoujinDetails>



    // Search with Included Tags (AND) + Excluded Tags (OR)
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
	        GROUP BY doujinId)
    """
    )
    suspend fun searchIncludedAndExcludedOr(
        includedTagsId: List<Long>,
        excludedTagsId: List<Long>,
        includedTagsCount: Int
    ): List<DoujinDetails>


    // Search with Included Tags (AND) + Excluded Tags (AND)
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
    suspend fun searchIncludedAndExcludedAnd(
        includedTagsId: List<Long>,
        excludedTagsId: List<Long>,
        includedTagsCount: Int,
        excludedTagsCount: Int
    ): List<DoujinDetails>
}