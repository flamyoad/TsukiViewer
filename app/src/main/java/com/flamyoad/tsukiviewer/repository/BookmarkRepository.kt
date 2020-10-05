package com.flamyoad.tsukiviewer.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.BookmarkGroupDao
import com.flamyoad.tsukiviewer.db.dao.BookmarkItemDao
import com.flamyoad.tsukiviewer.model.BookmarkGroup
import com.flamyoad.tsukiviewer.model.BookmarkItem
import org.threeten.bp.Instant
import java.io.File

class BookmarkRepository(private val context: Context) {

    companion object {
        const val DEFAULT_BOOKMARK_GROUP = "Default Bookmark Group"
    }

    private val db: AppDatabase

    val groupDao: BookmarkGroupDao

    val itemDao: BookmarkItemDao

    init {
        db = AppDatabase.getInstance(context)
        groupDao = db.bookmarkGroupDao()
        itemDao = db.bookmarkItemDao()
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

    fun getAllGroups(): LiveData<List<BookmarkGroup>> {
        return groupDao.getAll()
    }

    fun groupNameExists(name: String): LiveData<Boolean> {
        return groupDao.exists(name)
    }

    suspend fun getAllCollectionsFrom(absolutePath: File): List<BookmarkGroup> {
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
    suspend fun wipeAndInsertNew(absolutePath: File, hashMap: HashMap<String, Boolean>): String {
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
                var deleteCount = 0
                for (name in namesOfCollectionsToRemoveFrom) {
                    val count = itemDao.delete(absolutePath, name)
                    deleteCount += count
                }

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
                Log.e("db", e.message)
                e.printStackTrace()
                return@withTransaction "Failed to add or remove current doujin"
            }
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

