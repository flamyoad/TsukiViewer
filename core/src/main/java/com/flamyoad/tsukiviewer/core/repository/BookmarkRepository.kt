package com.flamyoad.tsukiviewer.core.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import com.flamyoad.tsukiviewer.core.db.AppDatabase
import com.flamyoad.tsukiviewer.core.db.dao.BookmarkGroupDao
import com.flamyoad.tsukiviewer.core.db.dao.BookmarkItemDao
import com.flamyoad.tsukiviewer.core.model.BookmarkGroup
import com.flamyoad.tsukiviewer.core.model.BookmarkItem
import com.flamyoad.tsukiviewer.core.model.Doujin
import org.threeten.bp.Instant
import java.io.File

class BookmarkRepository(
    private val db: AppDatabase,
    val groupDao: BookmarkGroupDao,
    val itemDao: BookmarkItemDao
) {
    companion object {
        const val DEFAULT_BOOKMARK_GROUP = "Default Bookmark Group"
    }

    fun getAllItems(): LiveData<List<BookmarkItem>> {
        return itemDao.selectAll()
    }

    fun getAllItems(group: BookmarkGroup): LiveData<List<BookmarkItem>> {
        return itemDao.from(group.name)
    }

    suspend fun getAllItemsFrom(group: BookmarkGroup): List<BookmarkItem> {
        return itemDao.selectFrom(group.name)
    }

    suspend fun getAllItemsFrom(groupName: String): List<BookmarkItem> {
        return itemDao.selectFrom(groupName)
    }

    fun getGroup(name: String): LiveData<BookmarkGroup> {
        return groupDao.get(name)
    }

    fun getAllGroups(): LiveData<List<BookmarkGroup>> {
        return groupDao.getAll()
    }

    fun groupNameExists(name: String): LiveData<Boolean> {
        return groupDao.exists(name)
    }

    suspend fun getAllGroupsFrom(absolutePath: File): List<BookmarkGroup> {
        val collections = groupDao.getAllBlocking()
        val names = groupDao.getCollectionNamesFrom(absolutePath)

        for (collection in collections) {
            if (collection.name in names) {
                collection.isTicked = true
            }
        }
        return collections
    }

    suspend fun changeGroupName(oldName: String, newName: String) {
        return groupDao.changeName(oldName, newName)
    }

    suspend fun removeGroup(group: BookmarkGroup) {
        groupDao.delete(group)
    }

    suspend fun removeGroup(name: String) {
        groupDao.delete(name)
    }

    suspend fun getAllGroupsBlocking(): List<BookmarkGroup> {
        return groupDao.getAllBlocking()
    }

    suspend fun insertGroup(collection: BookmarkGroup) {
        groupDao.insert(collection)
    }

    suspend fun insertItem(item: BookmarkItem) {
        itemDao.insert(item)
    }

    suspend fun moveItemsTo(group: BookmarkGroup, itemsToBeMoved: List<BookmarkItem>): Int {
        val movedItems = db.withTransaction {
            itemDao.delete(itemsToBeMoved)

            val itemList = itemsToBeMoved.map { x ->
                BookmarkItem(
                    id = null,
                    absolutePath = x.absolutePath,
                    parentName = group.name,
                    dateAdded = x.dateAdded
                )
            }
            return@withTransaction itemDao.insert(itemList)
        }
        return movedItems.size
    }

    // Returns: Snackbar message to be shown to user indicating the number of insert and delete
    suspend fun wipeAndInsertNew(
        absolutePath: File,
        hashMap: HashMap<String, Boolean>
    ): String {
        val namesOfCollectionsToRemoveFrom = hashMap
            .filter { kvp -> kvp.value == false }
            .map { kvp -> kvp.key }

        val dateAdded = Instant.now().toEpochMilli()

        val itemsToInsert = hashMap
            .filter { kvp -> kvp.value == true }
            .map { kvp ->
                BookmarkItem(
                    id = null,
                    absolutePath = absolutePath,
                    parentName = kvp.key,
                    dateAdded = dateAdded
                )
            }

        return db.withTransaction {
            try {
                var insertCount = 0
                for (item in itemsToInsert) {
                    if (itemDao.exists(item.absolutePath, item.parentName)) {
                        continue
                    } else {
                        val insertedId = itemDao.insert(item)
                        if (insertedId > 0) {
                            insertCount++
                        }
                    }
                }

                var deleteCount = 0
                for (name in namesOfCollectionsToRemoveFrom) {
                    val count = itemDao.delete(absolutePath, name)
                    deleteCount += count
                }

                // Example message:  Added into 1 collection. Removed from 1 collection.
                val stringBuilder = StringBuilder()

                if (insertCount > 0) {
                    stringBuilder.append("Added into ${insertCount} ${getNoun(insertCount)}. ")
                }

                if (deleteCount > 0) {
                    stringBuilder.append("Removed from ${deleteCount} ${getNoun(deleteCount)}")
                }

                return@withTransaction stringBuilder.toString()

            } catch (e: Exception) {
                Log.e("db", e.message ?: "Unknown error")
                e.printStackTrace()
                return@withTransaction "Failed to add or remove current doujin"
            }
        }
    }

    suspend fun insertAllItems(doujinList: List<Doujin>, groupNames: List<String>): String {
        val dateAdded = Instant.now().toEpochMilli()

        val itemsToInsert = groupNames.flatMap { groupName ->
            doujinList.map { doujin ->
                BookmarkItem(
                    id = null,
                    absolutePath = doujin.path,
                    parentName = groupName,
                    dateAdded = dateAdded
                )
            }
        }

        return db.withTransaction {
            var insertCount = 0
            for (item in itemsToInsert) {
                if (itemDao.exists(item.absolutePath, item.parentName)) {
                    continue
                } else {
                    val insertedId = itemDao.insert(item)
                    if (insertedId > 0) {
                        insertCount++
                    }
                }
            }

            if (insertCount == 0) {
                return@withTransaction "No items are bookmarked"
            }

            val builder = StringBuilder()

            when {
                insertCount == 1 -> builder.append("$insertCount item has been bookmarked. ")
                insertCount > 1 -> builder.append("$insertCount items have been bookmarked. ")
            }

            val ignoreCount = doujinList.size - insertCount
            when {
                ignoreCount == 1 -> builder.append("$ignoreCount duplicate item ignored")
                ignoreCount > 1 -> builder.append("$ignoreCount duplicate items ignored")
            }

            return@withTransaction builder.toString()
        }
    }

    private fun getNoun(number: Int): String {
        if (number > 1) {
            return "collections"
        } else {
            return "collection"
        }
    }
}

