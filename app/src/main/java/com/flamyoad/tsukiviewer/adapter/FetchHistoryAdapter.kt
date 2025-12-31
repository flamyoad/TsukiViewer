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
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.core.network.FetchHistory
import com.flamyoad.tsukiviewer.core.network.FetchStatus


class FetchHistoryAdapter :
    ListAdapter<FetchHistory, FetchHistoryAdapter.FetchViewHolder>(HistoryItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FetchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fetcher_list_item, parent, false)
        return FetchViewHolder(view)
    }

    override fun onBindViewHolder(holder: FetchViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        val item = getItem(position)
        return item.hashCode().toLong()
    }

    inner class FetchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgStatus: ImageView = itemView.findViewById(R.id.imgStatus)
        private val txtFolderName: TextView = itemView.findViewById(R.id.txtDoujinName)
        private val txtPath: TextView = itemView.findViewById(R.id.txtPath)

        fun bindTo(history: FetchHistory) {
            txtFolderName.text = history.doujinName
            txtPath.text = history.dir.absolutePath

            val drawable = when (history.status) {
                FetchStatus.SUCCESS -> ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.ic_check_green
                )
                FetchStatus.NO_MATCH -> ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.ic_triangle
                )

                FetchStatus.ALREADY_EXISTS -> ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.ic_sd_card_gray_24dp
                )
                else -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_triangle)
            }

            Glide.with(itemView)
                .load(drawable)
                .into(imgStatus)
        }
    }
}

class HistoryItemCallback : DiffUtil.ItemCallback<FetchHistory>() {
    override fun areItemsTheSame(oldItem: FetchHistory, newItem: FetchHistory): Boolean {
        return oldItem.dir == newItem.dir
    }

    override fun areContentsTheSame(oldItem: FetchHistory, newItem: FetchHistory): Boolean {
        return oldItem == newItem
    }
}