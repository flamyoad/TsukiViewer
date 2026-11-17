package com.flamyoad.tsukiviewer.ui.reader.recents

import androidx.lifecycle.ViewModel
import com.flamyoad.tsukiviewer.core.db.dao.RecentTabDao
import com.flamyoad.tsukiviewer.core.model.RecentTab
import com.flamyoad.tsukiviewer.core.repository.DoujinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecentTabsViewModel @Inject constructor(
    private val recentTabDao: RecentTabDao,
    private val library: DoujinRepository
) : ViewModel() {

    val tabList = recentTabDao.getAll()

    suspend fun close(tab: RecentTab): Boolean = withContext(Dispatchers.IO) {
        recentTabDao.delete(tab) > 0
    }

    suspend fun closeTabs(keepTabId: Long?) = withContext(Dispatchers.IO) {
        recentTabDao.deleteAllExcept(keepTabId ?: -1L)
    }

    suspend fun restore(tab: RecentTab): Long = recentTabDao.restore(tab)

    suspend fun loadLibrary(): List<RecentTab> = withContext(Dispatchers.IO) {
        library.localBookCovers().map { (folder, cover) ->
            RecentTab(title = folder.name, dirPath = folder, thumbnail = cover)
        }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })
    }

    suspend fun open(book: RecentTab): Long? = withContext(Dispatchers.IO) {
        if (!book.thumbnail.isFile) return@withContext null
        recentTabDao.open(book)
    }
}
