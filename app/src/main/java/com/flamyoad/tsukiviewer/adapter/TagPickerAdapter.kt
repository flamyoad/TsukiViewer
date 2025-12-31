package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.core.model.Tag
import com.flamyoad.tsukiviewer.ui.search.TagSelectedListener
import java.util.*

class TagPickerAdapter(private val listener: TagSelectedListener):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM = 1
        const val EMPTY = 2
    }

    private var tagListFull: List<Tag> = emptyList()

    private var tagListFiltered: List<Tag> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM) {
            val layout = LayoutInflater.from(parent.context)
                .inflate(R.layout.tag_picker_item, parent, false)

            val holder = TagViewHolder(layout)

            layout.setOnClickListener {
                val tag = tagListFiltered[holder.adapterPosition]
                listener.onTagSelected(tag)
            }
            return holder

        } else if (viewType == EMPTY) {
            val layout = LayoutInflater.from(parent.context)
                .inflate(R.layout.no_result_found, parent, false)

            val holder = EmptyViewHolder(layout)
            return holder
        }

        throw IllegalStateException("Illegal view type")
    }

    override fun getItemCount(): Int {
        if (tagListFiltered.size > 0) {
            return tagListFiltered.size
        } else {
            return 1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val tagHolder = holder as TagViewHolder
                tagHolder.bind(tagListFiltered[position])
            }
            EMPTY -> {
                // do nothing
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (tagListFiltered.isNotEmpty()) {
            return ITEM
        } else {
            return EMPTY
        }
    }

    fun setList(list: List<Tag>) {
        tagListFull = list
        tagListFiltered = list
        notifyDataSetChanged()
    }

    fun addFilter(keyword: String) {
        if (keyword.isEmpty()) {
            tagListFiltered = tagListFull
            notifyDataSetChanged()

            return
        }

        val list = tagListFull
            .filter { x -> x.name.toLowerCase(Locale.ROOT).contains(keyword) }

        tagListFiltered = list
        notifyDataSetChanged()
    }

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtTagName: TextView = itemView.findViewById(R.id.txtTagName)

        fun bind(tag: Tag) {
            txtTagName.text = tag.name
        }
    }

    inner class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}