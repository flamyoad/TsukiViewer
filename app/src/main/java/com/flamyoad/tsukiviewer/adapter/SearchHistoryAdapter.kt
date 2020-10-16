package com.flamyoad.tsukiviewer.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.SearchHistory
import com.flamyoad.tsukiviewer.utils.EmptyViewHolder

private const val TITLE_ONLY = 1
private const val TAGS_ONLY = 2
private const val TITLE_AND_TAGS = 3

private const val PLACEHOLDER = 4

class SearchHistoryAdapter(
    val onItemClick: (SearchHistory) -> Unit
) : PagedListAdapter<SearchHistory, RecyclerView.ViewHolder>(SearchHistoryCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = when (viewType) {
            TITLE_ONLY -> inflater.inflate(R.layout.search_history_item_only_title, parent, false)
            TAGS_ONLY -> inflater.inflate(R.layout.search_history_item_only_tags, parent, false)
            TITLE_AND_TAGS -> inflater.inflate(R.layout.search_history_item, parent, false)
            PLACEHOLDER -> inflater.inflate(R.layout.search_history_placeholder, parent, false)
            else -> throw IllegalArgumentException("View type does not exist")
        }

        val holder = when (viewType) {
            TITLE_ONLY -> TitleHolder(view)
            TAGS_ONLY -> TagHolder(view)
            TITLE_AND_TAGS -> TitleAndTagsHolder(view)
            PLACEHOLDER -> EmptyViewHolder(view)
            else -> throw IllegalArgumentException("View type does not exist")
        }

        view.setOnClickListener {
            val item = getItem(holder.bindingAdapterPosition) ?: return@setOnClickListener
            onItemClick(item)
        }

        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        when (holder.itemViewType) {
            TITLE_ONLY -> (holder as TitleHolder).bind(item)
            TAGS_ONLY -> (holder as TagHolder).bind(item)
            TITLE_AND_TAGS -> (holder as TitleAndTagsHolder).bind(item)
        }
        Log.d("debugf", "onBind called for position $position")
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position) ?: return PLACEHOLDER

        return when {
            item.title.isNotBlank() && item.tags.isNotBlank() -> TITLE_AND_TAGS
            item.title.isNotBlank() -> TITLE_ONLY
            else -> TAGS_ONLY
        }
    }

    inner class TitleAndTagsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtTags: TextView = itemView.findViewById(R.id.txtTags)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(history: SearchHistory) {
            txtTitle.text = history.title
            txtTags.text = history.tags
        }
    }

    inner class TitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(history: SearchHistory) {
            txtTitle.text = history.title
        }
    }

    inner class TagHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtTags: TextView = itemView.findViewById(R.id.txtTags)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(history: SearchHistory) {
            txtTags.text = history.tags
        }
    }
}

class SearchHistoryCallback : DiffUtil.ItemCallback<SearchHistory>() {
    override fun areItemsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean {
        return oldItem == newItem
    }
}

