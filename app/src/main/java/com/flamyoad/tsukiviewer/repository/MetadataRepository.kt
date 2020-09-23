package com.flamyoad.tsukiviewer.repository

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebSettings
import android.widget.Toast
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.*
import com.flamyoad.tsukiviewer.model.DoujinDetails
import com.flamyoad.tsukiviewer.model.DoujinTag
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.network.Metadata
import com.flamyoad.tsukiviewer.network.NHService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException

class MetadataRepository(private val context: Context) {

    private lateinit var nhService: NHService

    private val db: AppDatabase

    val pathDao: IncludedPathDao
    val doujinDetailsDao: DoujinDetailsDao
    val tagDao: TagDao
    val doujinTagDao: DoujinTagsDao
    val folderDao: IncludedFolderDao

    init {
        initializeNetwork()

        db = AppDatabase.getInstance(context)

        pathDao = db.includedFolderDao()
        doujinDetailsDao = db.doujinDetailsDao()
        tagDao = db.tagsDao()
        doujinTagDao = db.doujinTagDao()
        folderDao = db.folderDao()
    }

    private fun initializeNetwork() {
        // Nhentai API refuses the requests if the user agent is not attached.

        val dispatcher = Dispatcher().apply {
            maxRequests = 3
        }

        val httpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                    val request = chain.request()
                        .newBuilder()
                        .removeHeader("User-Agent")
                        .addHeader("User-Agent", WebSettings.getDefaultUserAgent(context))
                        .build()

                    return chain.proceed(request)
                }
            })
            .dispatcher(dispatcher)
            .build()

        val retrofit = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(NHService.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        nhService = retrofit.create(NHService::class.java)
    }

    suspend fun fetchMetadata(dir: File) {
        withContext(Dispatchers.IO) {
            if (doujinDetailsDao.existsByTitle(dir.name) ||
                doujinDetailsDao.existsByAbsolutePath(dir.toString())
            ) {
                // Should be updating existing instead of just doing nothing
                return@withContext
            }

            val response = getDataFromApi(dir.name)

            if (response != null && response.result.isNotEmpty()) {
                storeMetadata(response, dir)
            } else {
                Log.d("retrofit", "Can't find this sauce in NH.net")
            }

        }
    }

    suspend fun fetchMetadataAll(dirList: List<File>): Int {
        var itemsFetched = 0

        for (dir in dirList) {
            fetchMetadata(dir)

            itemsFetched++

            delay(1000)
        }
        return itemsFetched
    }

    private suspend fun storeMetadata(metadata: Metadata, dir: File) {
        // Api might return a list of duplicate results. We only want the first one
        val item = metadata.result.first()

        val doujinDetails = DoujinDetails(
            nukeCode = item.nukeCode,
            fullTitleEnglish = item.title.english,
            fullTitleJapanese = item.title.japanese ?: "",
            shortTitleEnglish = item.title.pretty ?: "",
            absolutePath = dir,
            folderName = dir.name
        )

        val doujinId = doujinDetailsDao.insert(doujinDetails)

        for (tag in item.tags) {
            var tagId: Long

            if (tagDao.exists(tag.type, tag.name)) {
                tagDao.incrementCount(tag.type, tag.name)
                tagId = tagDao.getId(tag.type, tag.name)

            } else {
                tagId = tagDao.insert(
                    Tag(
                        tagId = null,
                        name = tag.name,
                        type = tag.type,
                        url = tag.url,
                        count = 1
                    )
                )
            }

            doujinTagDao.insert(
                DoujinTag(doujinId, tagId)
            )
        }
    }

    // Erases previous existing tags and adds new ones
    suspend fun storeMetadata(doujinDetails: DoujinDetails, tags: List<Tag>) {
        withContext(Dispatchers.IO) {
            // todo: Rename this garbage
            val absolutePath = doujinDetails.absolutePath.absolutePath

            var doujinId: Long

            // Insert into db if a record identified by its absolute path does not exist yet
            if (!doujinDetailsDao.existsByAbsolutePath(absolutePath)) {
                doujinId = doujinDetailsDao.insert(doujinDetails)
            } else {
                val fetchedItems: List<DoujinDetails> =
                    doujinDetailsDao.findByAbsolutePath(absolutePath)
                doujinId = fetchedItems.first().id ?: -1
            }

            // Decrements book count for all tags in the previous data
            doujinTagDao.decrementTagCount(doujinId)

            // Removes all rows related to chosen id in the table
            doujinTagDao.deleteAll(doujinId)

            // Inserts new tag if has any, increments count for tags that already exist
            tags.forEach { tag ->
                val tagId: Long

                if (tagDao.exists(tag.type, tag.name)) {
                    tagDao.incrementCount(tag.type, tag.name)
                    tagId = tagDao.getId(tag.type, tag.name)

                } else {
                    tagId = tagDao.insert(
                        Tag(
                            tagId = null,
                            name = tag.name,
                            type = tag.type,
                            url = tag.url,
                            count = 1
                        )
                    )
                }
                doujinTagDao.insert(
                    DoujinTag(doujinId, tagId)
                )
            }
        }
    }

    suspend fun resetTags(dir: File) {
        withContext(Dispatchers.IO) {
            val response = getDataFromApi(dir.name)

            if (response != null && response.result.isNotEmpty()) {
                val result = response.result.first()

                val doujinDetails = doujinDetailsDao
                    .findByAbsolutePath(dir.absolutePath)
                    .first()

                val tagList = result.tags
                    .map { x -> Tag(type = x.type, name = x.name, url = x.url, count = 1) }

                storeMetadata(doujinDetails, tagList)
            } else {
                Log.d("retrofit", "Can't find this sauce in NH.net")
            }
        }
    }

    private fun getDataFromApi(fullTitle: String): Metadata? {
        Log.d("retrofit", "title: $fullTitle")

        // Wraps query parameter with double quotes to perform exact search
        try {
            val response = nhService.getMetadata("\"" + fullTitle + "\"")
                .execute()
            return response.body()

        } catch (e: IOException) {
            Log.d("retrofit", e.message)
            showToast("Failed to fetch metadata")

            e.printStackTrace()
        }
        return null
    }

    fun getDoujinShortName(dirPath: File): String {
        val results = doujinDetailsDao.findShortTitleByPath(dirPath.absolutePath)
        return results.firstOrNull() ?: ""
    }

    // Need this because we are running in non-UI thread
    private fun showToast(message: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT)
                .show()
        }
    }
}