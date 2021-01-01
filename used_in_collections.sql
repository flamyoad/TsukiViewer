1. Search with Included Tags (OR)
SELECT * FROM doujin_details as details
INNER JOIN doujin_tags ON details.id = doujin_tags.doujinId
WHERE tagId IN (51) ----> Replace with list args
GROUP BY details.id

2. Search with Included Tags (AND)
SELECT * FROM doujin_tags
INNER JOIN doujin_details ON doujin_tags.doujinId = doujin_details.id
WHERE doujin_tags.tagId IN (50,2,3)  ----> Replace with list args
GROUP BY doujin_tags.doujinId
HAVING COUNT(*) = 3   -----> Replace with list size

3. Search with Excluded Tags (OR)
SELECT * FROM doujin_details WHERE id IN (
	SELECT doujinId FROM doujin_tags
		EXCEPT
	SELECT doujinId FROM doujin_tags
	WHERE doujin_tags.tagId IN (1,3)
	GROUP BY doujin_tags.doujinId
) 

4. Search with Excluded Tags (AND)
SELECT * FROM doujin_details WHERE id IN (
	SELECT doujinId FROM doujin_tags
		EXCEPT
	SELECT doujinId FROM doujin_tags
	INNER JOIN doujin_details ON doujin_tags.doujinId = doujin_details.id
	WHERE doujin_tags.tagId IN (1) 
	GROUP BY doujin_tags.doujinId
	HAVING COUNT(*) = 1  -----> Replace with list size
) 

5. Search with Title + Included Tags (OR)
SELECT * FROM doujin_details as details
INNER JOIN doujin_tags ON details.id = doujin_tags.doujinId
WHERE tagId IN (51) AND (fullTitleEnglish LIKE '%Kotori%' OR fullTitleJapanese LIKE '%Kotori%')
GROUP BY details.id

6. Search with Title + Included Tags (AND)
SELECT * FROM doujin_tags
INNER JOIN doujin_details ON doujin_tags.doujinId = doujin_details.id
WHERE doujin_tags.tagId IN (50,2,3) AND (fullTitleEnglish LIKE '%Kotori%' OR fullTitleJapanese LIKE '%Kotori%')
GROUP BY doujin_tags.doujinId
HAVING COUNT(*) = 3   -----> Replace with list size

7. Search with Title + Excluded Tags (OR)
SELECT * FROM doujin_details WHERE 
(fullTitleEnglish LIKE '%Takami%' OR fullTitleJapanese LIKE '%Takami%') AND
 id IN (
	SELECT doujinId FROM doujin_tags
		EXCEPT
	SELECT doujinId FROM doujin_tags
	WHERE doujin_tags.tagId IN (1,3)
	GROUP BY doujin_tags.doujinId
) 

8. Search with Title + Excluded Tags (AND)
SELECT * FROM doujin_details WHERE 
(fullTitleEnglish LIKE '%Takami%' OR fullTitleJapanese LIKE '%Takami%') AND
 id IN (
	SELECT doujinId FROM doujin_tags
		EXCEPT
	SELECT doujinId FROM doujin_tags
	INNER JOIN doujin_details ON doujin_tags.doujinId = doujin_details.id
	WHERE doujin_tags.tagId IN (2,7,12,13,50,51,52,53,54,55,56) 
	GROUP BY doujin_tags.doujinId
	HAVING COUNT(*) = 11  -----> Replace with list size
) 

9. Search with Included Tags (OR) + Excluded Tags (OR)
SELECT * FROM doujin_details 
WHERE 
id IN (
	SELECT doujinId FROM doujin_tags
	WHERE doujin_tags.tagId IN (3) 
	GROUP BY doujinId
		EXCEPT 
	SELECT doujinId FROM doujin_tags
	WHERE doujin_tags.tagId IN (4)
	GROUP BY doujin_tags.doujinId
)

10. Search with Included Tags (OR) + Excluded Tags (AND)
SELECT * FROM doujin_details 
WHERE 
id IN (
	SELECT doujinId FROM doujin_tags
	WHERE doujin_tags.tagId IN (15) 
	GROUP BY doujinId
		EXCEPT 
	SELECT doujinId FROM doujin_tags
	WHERE doujin_tags.tagId IN (1,2)
	GROUP BY doujinId
	HAVING COUNT(*) = 2  -----> Replace with list size
)

11. Search with Included Tags (AND) + Excluded Tags (OR)

12. Search with Included Tags (AND) + Excluded Tags (AND)

13. Search with Title + Included Tags (OR) + Excluded Tags (OR)

14. Search with Title + Included Tags (OR) + Excluded Tags (AND)

15. Search with Title + Included Tags (AND) + Excluded Tags (OR)

16. Search with Title + Included Tags (AND) + Excluded Tags (AND)

Title , Included Tags, Excluded Tags
