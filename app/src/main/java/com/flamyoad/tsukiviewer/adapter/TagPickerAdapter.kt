package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.Tag

class TagPickerAdapter:
    RecyclerView.Adapter<TagPickerAdapter.TagViewHolder>() {

    private var tagList: List<Tag> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.tag_picker_dialog, parent, false)

        val holder = TagViewHolder(layout)

        layout.setOnClickListener {

        }

        return holder
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(tagList[position])
    }

    fun setList(list: List<Tag>) {
        tagList = list
    }

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtTagName: TextView = itemView.findViewById(R.id.txtTagName)

        fun bind(tag: Tag) {
            txtTagName.text = tag.name
        }
    }
}