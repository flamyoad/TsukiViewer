package com.flamyoad.tsukiviewer.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.withTransaction
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.CollectionItemDao
import com.flamyoad.tsukiviewer.db.dao.DoujinCollectionDao
import com.flamyoad.tsukiviewer.model.CollectionItem
import com.flamyoad.tsukiviewer.model.DoujinCollection
import java.io.File
import java.lang.StringBuilder

class CollectionRepository(private val context: Context) {

    companion object {
        const val DEFAULT_COLLECTION = "Default Collection"
    }

    private val db: AppDatabase

    val collectionDao: DoujinCollectionDao

    val itemDao: CollectionItemDao

    init {
        db = AppDatabase.getInstance(context)
        collectionDao = db.doujinCollectionDao()
        itemDao = db.collectionItemDao()
    }

    fun getAllItems(): LiveData<List<CollectionItem>> {
        return itemDao.selectAll()
    }

    suspend fun getAllItemsFrom(collection: DoujinCollection): List<CollectionItem> {
        return itemDao.selectFrom(collection.name)
    }

    fun getAllCollections(): LiveData<List<DoujinCollection>> {
        return collectionDao.getAll()
    }

    fun collectionNameExists(name: String): LiveData<Boolean> {
        return collectionDao.exists(name)
    }

    suspend fun getAllCollectionsFrom(absolutePath: File): List<DoujinCollection> {
        val collections = collectionDao.getAllBlocking()
        val names = collectionDao.getCollectionNamesFrom(absolutePath)

        for (collection in collections) {
            if (collection.name in names) {
                collection.isTicked = true
            }
        }
        return collections
    }

    suspend fun changeCollectionName(oldName: String, newName: String) {
        collectionDao.changeName(oldName, newName)
    }

    suspend fun deleteCollection(collection: DoujinCollection) {
        collectionDao.delete(collection)
    }

    suspend fun deleteCollection(name: String) {
        collectionDao.delete(name)
    }

    suspend fun getAllCollectionsBlocking(): List<DoujinCollection> {
        return collectionDao.getAllBlocking()
    }

    // Inserts a default folder first.
    suspend fun createDefaultCollection() {
        collectionDao.insert(DoujinCollection(DEFAULT_COLLECTION))
    }

    suspend fun insertCollection(collection: DoujinCollection) {
        collectionDao.insert(collection)
    }

    suspend fun insertItem(item: CollectionItem) {
        itemDao.insert(item)
    }

    // Returns: Snackbar message to be shown to user indicating the number of insert and delete
    suspend fun wipeAndInsertNew(absolutePath: File, hashMap: HashMap<String, Boolean>): String {
        val namesOfCollectionsToRemoveFrom = hashMap
            .filter { kvp -> kvp.value == false   }
            .map { kvp -> kvp.key }

        val itemsToInsert = hashMap
            .filter { kvp -> kvp.value == true  }
            .map { kvp -> CollectionItem(absolutePath = absolutePath, collectionName = kvp.key) }

        return db.withTransaction {
            try {
                var deleteCount = 0
                for (name in namesOfCollectionsToRemoveFrom) {
                    val count = itemDao.delete(absolutePath, name)
                    deleteCount += count
                }

                val insertIds = itemDao.insert(itemsToInsert)
                val insertCount = insertIds.size

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

