package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.SearchHistory

class SearchHistoryAdapter
    : RecyclerView.Adapter<SearchHistoryAdapter.SearchViewHolder>() {

    private var list: List<SearchHistory> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_history_item, parent, false)

        val holder = SearchViewHolder(layout)

        layout.setOnClickListener {
            val position = holder.adapterPosition
        }

        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun setList(list: List<SearchHistory>) {
        this.list = list
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtTags: TextView = itemView.findViewById(R.id.txtTags)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(history: SearchHistory) {
            txtTitle.text = history.title
            txtTags.text = history.tags
        }
    }
}