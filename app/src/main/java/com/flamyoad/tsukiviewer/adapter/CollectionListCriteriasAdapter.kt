package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.CollectionCriteria
import com.flamyoad.tsukiviewer.ui.home.collections.DialogTagPicker
import com.google.android.material.chip.Chip

class CollectionListCriteriasAdapter(
    private val list: List<CollectionCriteria>,
    private val mode: DialogTagPicker.Mode
) :
    RecyclerView.Adapter<CollectionListCriteriasAdapter.TagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        when (mode) {
            DialogTagPicker.Mode.Inclusive -> {
                val layout = inflater.inflate(R.layout.collection_included_tag_item, parent, false)
                return TagViewHolder(layout)
            }
            DialogTagPicker.Mode.Exclusive -> {
                val layout = inflater.inflate(R.layout.collection_excluded_tag_item, parent, false)
                return TagViewHolder(layout)
            }
            else -> throw IllegalArgumentException("Enums other than INCLUSIVE and EXCLUSIVE are passed in constructor")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chip: Chip = itemView.findViewById(R.id.chip)

        fun bind(tag: CollectionCriteria) {
            chip.text = tag.valueName
        }
    }

}