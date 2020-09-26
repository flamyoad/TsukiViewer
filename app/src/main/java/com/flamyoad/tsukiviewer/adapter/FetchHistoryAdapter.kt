package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.network.FetchHistory
import com.flamyoad.tsukiviewer.network.FetchStatus

class FetchHistoryAdapter: RecyclerView.Adapter<FetchHistoryAdapter.FetchViewHolder>() {

    private var itemList = emptyList<FetchHistory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FetchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fetcher_list_item, parent, false)

        val holder = FetchViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FetchViewHolder, position: Int) {
        holder.bindTo(itemList[position])
    }

    override fun getItemId(position: Int): Long {
        val item = itemList[position]
        return item.hashCode().toLong()
    }

    fun setList(list: List<FetchHistory>) {
        itemList = list
        notifyDataSetChanged()
    }

    inner class FetchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgStatus: ImageView = itemView.findViewById(R.id.imgStatus)
        private val txtFolderName: TextView = itemView.findViewById(R.id.txtDoujinName)
        private val txtPath: TextView = itemView.findViewById(R.id.txtPath)

        fun bindTo(history: FetchHistory) {
            txtFolderName.text = history.doujinName
            txtPath.text = history.dir.absolutePath

            val drawable = when (history.status) {
                FetchStatus.SUCCESS -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_check_green)
                FetchStatus.NO_MATCH -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_triangle)
                else -> ContextCompat.getDrawable(itemView.context, R.drawable.ic_triangle)
            }

            Glide.with(itemView)
                .load(drawable)
                .into(imgStatus)
        }
    }
}