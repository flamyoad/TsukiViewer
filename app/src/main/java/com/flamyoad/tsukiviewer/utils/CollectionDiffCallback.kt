package com.flamyoad.tsukiviewer.utils

import androidx.recyclerview.widget.DiffUtil
import com.flamyoad.tsukiviewer.model.CollectionItem

class CollectionDiffCallback: DiffUtil.ItemCallback<CollectionItem>() {

    override fun areItemsTheSame(oldItem: CollectionItem, newItem: CollectionItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CollectionItem, newItem: CollectionItem): Boolean {
        return oldItem == newItem
    }
}