package com.flamyoad.tsukiviewer.repository

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebSettings
import android.widget.Toast
import androidx.room.withTransaction
import com.flamyoad.tsukiviewer.db.AppDatabase
import com.flamyoad.tsukiviewer.db.dao.*
import com.flamyoad.tsukiviewer.model.DoujinDetails
import com.flamyoad.tsukiviewer.model.DoujinTag
import com.flamyoad.tsukiviewer.model.Source
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.network.*
import com.flamyoad.tsukiviewer.network.api.FakkuService
import com.flamyoad.tsukiviewer.network.api.HenNexusService
import com.flamyoad.tsukiviewer.network.api.NHService
import com.flamyoad.tsukiviewer.parser.HenNexusParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.*

class MetadataRepository(private val context: Context) {
    private lateinit var nhService: NHService
    private lateinit var henNexusService: HenNexusService
    private lateinit var fakkuService: FakkuService

    private val henNexusParser: HenNexusParser by lazy { HenNexusParser() }

    private val db: AppDatabase

    val pathDao: IncludedPathDao
    val doujinDetailsDao: DoujinDetailsDao
    val tagDao: TagDao
    val doujinTagDao: DoujinTagsDao
    val folderDao: IncludedFolderDao

    /*
        Regex used to remove text between parentheses and brackets
        Before : (C97) [Batsu Jirushi (Batsu)] AzuLan Shikoshiko Bokou Seikatsu (Azur Lane) [English] [AntaresNL667]
        After :  AzuLan Shikoshiko Bokou Seikatsu
    */
    private val regex by lazy {
        "\\[.*?]|\\(.*?\\)".toRegex()
    }

    private var toast: Toast? = null

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
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .dispatcher(dispatcher)
            .build()

        val nhBuilder = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(NHService.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        nhService = nhBuilder.create(NHService::class.java)

        val henNexusBuilder = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(HenNexusService.baseUrl)
            .build()

        henNexusService = henNexusBuilder.create(HenNexusService::class.java)

//        val fakkuBuilder = Retrofit.Builder()
//            .client(httpClient)
//            .baseUrl(FakkuService.baseUrl)
//            .build()
//
//        fakkuService = fakkuBuilder.create(FakkuService::class.java)
    }

    suspend fun fetchMetadata(dir: File, sources: EnumSet<Source>): FetchHistory {
        return withContext(Dispatchers.IO) {
            if (doujinDetailsDao.existsByTitle(dir.name) || doujinDetailsDao.existsByAbsolutePath(dir.toString())) {
                return@withContext FetchHistory(dir, dir.name, FetchStatus.ALREADY_EXISTS)

            } else {
                val fetchResult = requestMetadata(dir.name, sources)
                val title = fetchResult.getDoujinTitle() ?: dir.name

                val metadata = fetchResult.metadata

                if (metadata == null) {
                    return@withContext FetchHistory(dir, title, FetchStatus.NO_MATCH)
                } else {
                    saveMetadata(metadata, dir)
                    return@withContext FetchHistory(dir, title, fetchResult.status)
                }
            }
        }
    }

    private fun requestMetadata(fullTitle: String, sources: EnumSet<Source>): FetchResult {
        var cleanedTitle: String = ""

        if (sources.contains(Source.NHentai)) {
            // First try is attempted with title which contains all these , | [] {} symbols
            val queryNhentaiWithDirName = requestFromNhentai(fullTitle)
            Log.d("fetchbug", "Query NHentai with Dir Name")
            if (queryNhentaiWithDirName.status == FetchStatus.SUCCESS) {
                return queryNhentaiWithDirName
            }

            cleanedTitle = fullTitle.replace(regex, "")
            if (cleanedTitle == fullTitle) {
                return queryNhentaiWithDirName // No point retrying if same string
            }

            // Retry with cleaned title without symbols
            Log.d("fetchbug", "Query NHentai with Cleaned Name")
            val queryNhentaiWithCleanTitle = requestFromNhentai(cleanedTitle)
            if (queryNhentaiWithCleanTitle.status == FetchStatus.SUCCESS) {
                return queryNhentaiWithCleanTitle
            }
        }

//        if (sources.contains(Source.HentaiNexus)) {
//            Log.d("fetchbug", "Query HentaiNexus with Cleaned Name")
//            if (cleanedTitle == "") {
//                cleanedTitle = fullTitle.replace(regex, "")
//            }
//
//            return requestFromHenNexus(cleanedTitle)
//        }

        Log.d("fetchbug", "No match")
        return FetchResult(null, FetchStatus.NO_MATCH)
    }

    private fun requestFromNhentai(fullTitle: String): FetchResult {
        try {
            // Wraps query parameter with double quotes to perform exact search
            val response = nhService.getMetadata("\"" + fullTitle + "\"").execute()
            val json = response.body() ?: return FetchResult(status = FetchStatus.NETWORK_ERROR)

            return when (json.result.isEmpty()) {
                true -> FetchResult(status = FetchStatus.NO_MATCH)
                false -> FetchResult(json, status = FetchStatus.SUCCESS)
            }

        } catch (e: IOException) {
            showToast("Failed to fetch metadata")
            return FetchResult(status = FetchStatus.NETWORK_ERROR)
        }
    }

    private fun requestFromHenNexus(fullTitle: String): FetchResult {
        try {
            val searchRequest = henNexusService.getSearchResult(fullTitle).execute()
            val searchResultHtml = searchRequest.body()?.string()

            if (searchResultHtml.isNullOrBlank()) {
                return FetchResult(status = FetchStatus.NO_MATCH)
            }

            val firstItemLink = henNexusParser.getFirstItemInList(searchResultHtml)

            val firstItemRequest = henNexusService.getPageUrl(firstItemLink).execute()
            val firstItemHtml = firstItemRequest.body()?.string()

            if (firstItemHtml.isNullOrBlank()) {
                return FetchResult(status = FetchStatus.NO_MATCH)
            }

            val metadata = henNexusParser.parseItem(firstItemHtml)

            if (!metadata.hasValue) {
                return FetchResult(status = FetchStatus.NO_MATCH)
            } else {
                return FetchResult(metadata, status = FetchStatus.SUCCESS)
            }

        } catch (e: IOException) {
            showToast("Failed to fetch metadata")
            return FetchResult(status = FetchStatus.NETWORK_ERROR)
        }
    }

    private suspend fun saveMetadata(metadata: Metadata, dir: File): String {
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

        db.withTransaction {
            val doujinId = doujinDetailsDao.insert(doujinDetails)

            for (tag in item.tags) {
                var tagId: Long

                if (tag.name.trim() == "" || tag.name == "null")
                    continue

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
                doujinTagDao.insert(DoujinTag(doujinId, tagId))
            }
        }
        return item.title.english
    }

    // Erases previous existing tags and adds tags edited by user
    suspend fun saveEditedMetadata(doujinDetails: DoujinDetails, tags: List<Tag>) {
        withContext(Dispatchers.IO) {
            db.withTransaction {
                val absolutePath = doujinDetails.absolutePath.absolutePath

                val doujinId: Long

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
                doujinTagDao.deleteFromDoujin(doujinId)

                // Inserts new tag if has any, increments count for tags that already exist
                tags.forEach { tag ->
                    insertTagElseIncrement(doujinId, tag)
                }
            }
        }
    }

    private suspend fun insertTagElseIncrement(doujinId: Long, tag: Tag) {
        db.withTransaction {
            val tagId: Long

            if (tag.name.trim() == "" || tag.name == "null")
                return@withTransaction

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

    suspend fun removeMetadata(doujinDetails: DoujinDetails) {
        withContext(Dispatchers.IO) {
            db.withTransaction {
                doujinDetailsDao.delete(doujinDetails)
                doujinTagDao.deleteFromDoujin(doujinDetails.id!!)
                doujinTagDao.decrementTagCount(doujinDetails.id)
            }
        }
    }

    suspend fun resetTags(dir: File, sources: EnumSet<Source>) {
        withContext(Dispatchers.IO) {
//            val result = requestFromNhentai(dir.name)
            val result = requestMetadata(dir.name, sources)

            if (result.status == FetchStatus.SUCCESS) {
                val doujinDetails = doujinDetailsDao
                    .findByAbsolutePath(dir.absolutePath)
                    .first()

                if (result.metadata == null) return@withContext

                val tagList = result.metadata.getTags()
                    .map { x -> Tag(type = x.type, name = x.name, url = x.url, count = 1) }
                saveEditedMetadata(doujinDetails, tagList)
            } else {
                Log.d("retrofit", "Can't find this sauce")
            }
        }
    }

    // Need this because we are running in non-UI thread
    private fun showToast(message: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            toast?.cancel()

            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast?.show()
        }
    }
}