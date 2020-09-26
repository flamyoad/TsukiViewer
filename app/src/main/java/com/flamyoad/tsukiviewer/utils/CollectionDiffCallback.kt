package com.flamyoad.tsukiviewer.utils

import androidx.recyclerview.widget.DiffUtil
import com.flamyoad.tsukiviewer.model.CollectionItem

class CollectionDiffCallback: DiffUtil.ItemCallback<CollectionItem>() {

    override fun areItemsTheSame(oldItem: CollectionItem, newItem: CollectionItem): Boolean {
        val status = oldItem.id == newItem.id
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CollectionItem, newItem: CollectionItem): Boolean {
        val status =  oldItem.absolutePath == newItem.absolutePath
                && oldItem.collectionName == newItem.collectionName
                && oldItem.isHeader == newItem.isHeader
                && oldItem.doujin == newItem.doujin
                && oldItem.isCollapsed == newItem.isCollapsed
                && oldItem.isSelected == newItem.isSelected

        return status
    }
}