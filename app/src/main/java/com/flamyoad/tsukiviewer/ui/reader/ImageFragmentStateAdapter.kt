package com.flamyoad.tsukiviewer.ui.reader

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.io.File

class ImageFragmentStateAdapter(fm: FragmentManager)
    : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var imageList: List<File> = emptyList()

    fun setList(imageList: List<File>) {
        this.imageList = imageList
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        val image = imageList[position]
        return ImageFragment.newInstance(image)
    }

    override fun getCount(): Int {
        return imageList.size
    }
}