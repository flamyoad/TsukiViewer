package com.flamyoad.tsukiviewer.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flamyoad.tsukiviewer.ui.reader.ImageFragment
import java.io.File

class VerticalSwipeImageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private var imageList: List<File> = emptyList()

    fun setList(imageList: List<File>) {
        this.imageList = imageList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun createFragment(position: Int): Fragment {
        val image = imageList[position]
        return ImageFragment.newInstance(image)
    }
}