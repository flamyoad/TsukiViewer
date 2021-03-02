package com.flamyoad.tsukiviewer.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.ui.doujinpage.DoujinDetailsActivity
import com.flamyoad.tsukiviewer.ui.search.SearchActivity
import com.flamyoad.tsukiviewer.ui.search.SearchResultActivity

/*
    Class to simulate the deep activity stack consists of SearchResultActivity.kt and DoujinDetailsActivity.kt

    This class contains methods to resume activities destroyed by the CLEAR_TOP intents. Necessary data to restore
    the activities is stored in the self-defined Application class.

    This is necessary because without CLEAR_TOP, the deep activity stacks (etc 6 duplicate activites)
    would cause OOM exception
 */
object ActivityStackUtils {

    const val ACTIVITY_STACK_LIMIT = 4

    /*
    If @topClearedAtLeastOnce is true, means one of the activities in the stack has been started with CLEAR_TOP intent.
    We should use the information in the custom activity stack during onBackPress or android.R.id.home click
    to resume previous activities
     */
    private var topClearedAtLeastOnce: Boolean = false

    fun pushNewActivityIntent(context: Context, intent: Intent, emptyHistory: ActivityHistory) {
        val application = context.applicationContext as MyApplication

        val activityHistory = when (emptyHistory) {
            is ActivityHistory.DoujinDetailsActivity -> {
                ActivityHistory.DoujinDetailsActivity(
                    dirPath = intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH) ?: return,
                    doujinName = intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_NAME) ?: return
                )
            }
            is ActivityHistory.SearchResultActivity -> {
                ActivityHistory.SearchResultActivity(
                    tags = intent.getStringExtra(SearchActivity.SEARCH_TAGS) ?: "",
                    title = intent.getStringExtra(SearchActivity.SEARCH_TITLE) ?: "",
                    includeAllTags = intent.getBooleanExtra(SearchActivity.SEARCH_INCLUDE_ALL_TAGS, false)
                )
            }
        }
        application.activityStack.add(activityHistory)
    }

    fun popAndResumePrevActivity(context: Context) {
        val applicationContext = context.applicationContext

        val activityStack = (context.applicationContext as MyApplication).activityStack
        if (activityStack.isEmpty()) {
            return
        }

        if (activityStack.size <= ACTIVITY_STACK_LIMIT && !topClearedAtLeastOnce) {
            activityStack.removeAt(activityStack.size - 1) // Pop the last item
            return
        }

        val lastIndex = activityStack.size - 1
        val activityHistory = activityStack.removeAt(lastIndex)

        val intent = when (activityHistory) {
            is ActivityHistory.DoujinDetailsActivity -> {
                Intent(applicationContext, DoujinDetailsActivity::class.java).apply {
                    flags = FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH, activityHistory.dirPath)
                    putExtra(LocalDoujinsAdapter.DOUJIN_NAME, activityHistory.doujinName)
                }
            }
            is ActivityHistory.SearchResultActivity -> {
                Intent(applicationContext, SearchResultActivity::class.java).apply {
                    flags = FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(SearchActivity.SEARCH_TAGS, activityHistory.tags)
                    putExtra(SearchActivity.SEARCH_TITLE, activityHistory.title)
                    putExtra(SearchActivity.SEARCH_INCLUDE_ALL_TAGS, activityHistory.includeAllTags)
                }
            }
        }

        // android.util.AndroidRuntimeException: Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
//        applicationContext.startActivity(intent)

        context.startActivity(intent)

        // Ends the activity
        (context as Activity).finish()

        topClearedAtLeastOnce = true

        if (activityStack.isEmpty()) {
            topClearedAtLeastOnce = false // Re-initialize the variable
        }
    }

    fun shouldStartWithClearTop(context: Context): Boolean {
        val activityStack = (context.applicationContext as MyApplication).activityStack
        return activityStack.size > ACTIVITY_STACK_LIMIT
    }

}