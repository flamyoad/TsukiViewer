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

    fun getAllCollection(): LiveData<List<DoujinCollection>> {
        return collectionDao.getAll()
    }

    // Inserts a default folder first.
    suspend fun initializeCollection() {
        collectionDao.insert(DoujinCollection(DEFAULT_COLLECTION))
    }

    suspend fun insertCollection(collection: DoujinCollection) {
        collectionDao.insert(collection)
    }

    suspend fun insertItem(item: CollectionItem) {
        itemDao.insert(item)
    }

    suspend fun wipeAndInsertNew(absolutePath: File, collections: List<DoujinCollection>) {
        db.withTransaction {
            try {
                itemDao.deleteFromAllCollections(absolutePath)
                for (collection in collections) {
                    val newItem = CollectionItem(
                        absolutePath = absolutePath,
                        collectionName = collection.name
                    )
                    itemDao.insert(newItem)
                }
            } catch (e: Exception) {
                Log.d("db", e.message)
                e.printStackTrace()
            }
        }
    }
}

