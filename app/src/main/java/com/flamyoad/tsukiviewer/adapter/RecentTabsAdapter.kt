package com.flamyoad.tsukiviewer.adapter

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
import com.flamyoad.tsukiviewer.model.RecentTab

class RecentTabsAdapter(private val onTabClick: (RecentTab) -> Unit) :
    ListAdapter<RecentTab, RecentTabsAdapter.TabViewHolder>(TabDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TabViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.reader_tab_item, parent, false)

        val holder = TabViewHolder(layout)

        layout.setOnClickListener {
            val position = holder.bindingAdapterPosition
            onTabClick(getItem(position))
        }

        return holder
    }

    override fun onBindViewHolder(holder: TabViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getRecentTab(position: Int): RecentTab {
        return getItem(position)
    }

    inner class TabViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgScreenshot: ImageView = itemView.findViewById(R.id.imgScreenshot)
        private val txtDoujinTitle: TextView = itemView.findViewById(R.id.txtDoujinTitle)

        fun bind(tabHistory: RecentTab) {
            val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.himekawa)
            Glide.with(itemView.context)
                .load(drawable)
                .transition(withCrossFade())
                .into(imgScreenshot)

            txtDoujinTitle.text = tabHistory.title
        }
    }
}

class TabDiffUtil : DiffUtil.ItemCallback<RecentTab>() {
    override fun areItemsTheSame(oldItem: RecentTab, newItem: RecentTab): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RecentTab, newItem: RecentTab): Boolean {
        return oldItem == newItem
    }
}