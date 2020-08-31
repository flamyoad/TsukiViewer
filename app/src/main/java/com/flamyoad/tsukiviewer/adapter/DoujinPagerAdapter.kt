package com.flamyoad.tsukiviewer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.flamyoad.tsukiviewer.ui.doujinpage.FragmentDoujinDetails
import com.flamyoad.tsukiviewer.ui.doujinpage.FragmentGridImages
import java.lang.IllegalArgumentException
import java.lang.RuntimeException

class DoujinPagerAdapter(fm: FragmentManager)
    : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val NUM_ITEMS = 2

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FragmentDoujinDetails.newInstance()
            1 -> FragmentGridImages.newInstance()
            else -> throw IllegalArgumentException("Invalid index in fragment pager")
        }
    }

    override fun getCount(): Int {
        return NUM_ITEMS
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Details"
            1 -> "Images"
            else -> "null"
        }
    }
}