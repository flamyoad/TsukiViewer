package com.flamyoad.tsukiviewer.ui.reader.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flamyoad.tsukiviewer.core.model.RecentTab

class ReaderTabFragmentAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {

    private var tabList: List<RecentTab> = emptyList()

    private var dirPath: String = ""
    private var positionOfStartingImage: Int? = null

    override fun getItemCount(): Int {
        return tabList.size
    }

    fun setList(list: List<RecentTab>) {
        this.tabList = list
        notifyDataSetChanged()
    }

    fun setFirstItem(dirPath: String, startPosition: Int?) {
        this.dirPath = dirPath
        this.positionOfStartingImage = startPosition
    }

    override fun getItemId(position: Int): Long =
        requireNotNull(tabList[position].id)

    override fun containsItem(itemId: Long): Boolean = tabList.any { it.id == itemId }

    fun tabIdAt(position: Int): Long? = tabList.getOrNull(position)?.id

    fun getTabPosition(tabId: Long): Int {
        return tabList.indexOfFirst { tab -> tab.id == tabId }
    }

    fun getTab(path: String): RecentTab? {
        return tabList.firstOrNull { tab -> tab.dirPath.absolutePath == path }
    }

    fun getTab(id: Long): RecentTab? {
        return tabList.firstOrNull { tab -> tab.id == id }
    }

    override fun createFragment(position: Int): Fragment {
        val tab = tabList[position]
        val isOriginalBook = tab.dirPath.absolutePath == dirPath
        val startPosition = if (isOriginalBook) {
            positionOfStartingImage.also { positionOfStartingImage = null }
        } else {
            null
        }
        return ReaderTabFragment.newInstance(tab, startPosition ?: -1, isOriginalBook)
    }
}
