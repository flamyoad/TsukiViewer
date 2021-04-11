package com.flamyoad.tsukiviewer.ui.reader.tabs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flamyoad.tsukiviewer.model.RecentTab

class ReaderTabFragmentAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {

    private var tabList: List<RecentTab> = emptyList()

    private var dirPath: String = ""
    private var positionOfStartingFragment: Int = 0
    private var positionOfStartingImage: Int = 0

    override fun getItemCount(): Int {
        return tabList.size
    }

    fun setList(list: List<RecentTab>) {
        this.tabList = list
        positionOfStartingFragment = list.indexOfFirst { tab -> tab.dirPath.absolutePath == dirPath }
        notifyDataSetChanged()
    }

    fun setFirstItem(dirPath: String, startPosition: Int) {
        this.dirPath = dirPath
        this.positionOfStartingImage = startPosition
    }

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
        if (position == positionOfStartingFragment) {
            return ReaderTabFragment.newInstance(tabList[position], positionOfStartingImage, true)
        } else {
            return ReaderTabFragment.newInstance(tabList[position], 0, false)
        }
    }
}