package com.flamyoad.tsukiviewer.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.ReaderTabHistory

class ReaderTabAdapter :
    ListAdapter<ReaderTabHistory, ReaderTabAdapter.TabViewHolder>(TabDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.reader_tab_item, parent, false)

        val holder = TabViewHolder(layout)

        layout.setOnClickListener {
            val position = holder.bindingAdapterPosition
        }

        return holder
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgScreenshot: ImageView = itemView.findViewById(R.id.imgScreenshot)
        private val txtDoujinTitle: TextView = itemView.findViewById(R.id.txtDoujinTitle)

        fun bind(tabHistory: ReaderTabHistory) {
            val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.himekawa)
            Glide.with(itemView.context)
                .load(drawable)
                .transition(withCrossFade())
                .into(imgScreenshot)

            txtDoujinTitle.text = tabHistory.title
        }
    }
}

class TabDiffUtil : DiffUtil.ItemCallback<ReaderTabHistory>() {
    override fun areItemsTheSame(oldItem: ReaderTabHistory, newItem: ReaderTabHistory): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ReaderTabHistory, newItem: ReaderTabHistory): Boolean {
        return oldItem == newItem
    }
}