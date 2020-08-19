package com.flamyoad.tsukiviewer.repository

import android.app.Application
import android.util.Log
import android.webkit.WebSettings
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.DoujinDetailsDao
import com.flamyoad.tsukiviewer.db.dao.DoujinTagsDao
import com.flamyoad.tsukiviewer.db.dao.IncludedFolderDao
import com.flamyoad.tsukiviewer.db.dao.TagDao
import com.flamyoad.tsukiviewer.model.DoujinDetails
import com.flamyoad.tsukiviewer.model.DoujinTag
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.network.Metadata
import com.flamyoad.tsukiviewer.network.NHService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException

class MetadataRepository(private val application: Application) {

    private lateinit var nhService: NHService

    private val db: AppDatabase

    val folderDao: IncludedFolderDao
    val doujinDetailsDao: DoujinDetailsDao
    val tagDao: TagDao
    val doujinTagDao: DoujinTagsDao

    init {
        initializeNetwork()

        db = AppDatabase.getInstance(application)

        folderDao = db.includedFolderDao()
        doujinDetailsDao = db.doujinDetailsDao()
        tagDao = db.tagsDao()
        doujinTagDao = db.doujinTagDao()
    }

    private fun initializeNetwork() {
        // Nhentai API refuses the requests if the user agent is not attached. Hence the OkHTTPInterceptor
        val httpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                    val request = chain.request()
                        .newBuilder()
                        .removeHeader("User-Agent")
                        .addHeader("User-Agent", WebSettings.getDefaultUserAgent(application))
                        .build()

                    return chain.proceed(request)
                }
            }).build()

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

        withContext(Dispatchers.IO) {
            for (dir in dirList) {

                if (doujinDetailsDao.existsByTitle(dir.name) ||
                    doujinDetailsDao.existsByAbsolutePath(dir.toString())
                ) {
                    continue
                }

                val response = getDataFromApi(dir.name)

                if (response != null && response.result.isNotEmpty()) {
                    storeMetadata(response, dir)
                } else {
                    Log.d("retrofit", "Can't find this sauce in NH.net")
                }

                // Sleep 1 second
                itemsFetched++
                delay(1000)
            }
        }
        return itemsFetched
    }

    // Test the edge case where user moves the directory after details scanned into DB
    private suspend fun storeMetadata(metadata: Metadata, dir: File) {
        // Api might return a list of duplicate results. We only want the first one
        val item = metadata.result.first()

        val doujinDetails = DoujinDetails(
            nukeCode = item.nukeCode,
            fullTitleEnglish = item.title.english,
            fullTitleJapanese = item.title.japanese,
            shortTitleEnglish = item.title.pretty,
            absolutePath = dir,
            folderName = dir.name
        )

        val doujinId = doujinDetailsDao.insert(doujinDetails)

        for (tag in item.tags) {
            var tagId: Long? = null

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

    private fun getDataFromApi(fullTitle: String): Metadata? {
        Log.d("retrofit", "title: $fullTitle")

        // Wraps query parameter with double quotes to perform exact search
        try {
            val response = nhService.getMetadata("\"" + fullTitle + "\"")
                .execute()
            return response.body()

        } catch (e: IOException) {
            Log.d("retrofit", e.message)
            e.printStackTrace()
        }
        return null
    }


}