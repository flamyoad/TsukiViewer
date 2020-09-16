package com.flamyoad.tsukiviewer.ui.home.tags

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.flamyoad.tsukiviewer.model.TagType

class TagFragmentAdapter(activity: FragmentActivity, private val tagList: List<TagType>) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return tagList.size
    }

    override fun createFragment(position: Int): Fragment {
        val tag = tagList[position]
        return TagFragment.newInstance(tag)
    }

}