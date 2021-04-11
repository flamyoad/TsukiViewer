package com.flamyoad.tsukiviewer.utils

import androidx.recyclerview.selection.ItemKeyProvider
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter

class DoujinItemKeyProvider(private val adapter: LocalDoujinsAdapter): ItemKeyProvider<String>(SCOPE_CACHED) {
    override fun getKey(position: Int): String?  = ""
    override fun getPosition(key: String): Int = -1

//    override fun getKey(position: Int): String? = adapter.getItem(position).path.toString()
//    override fun getPosition(key: String): Int = adapter.getPosition(key)
}