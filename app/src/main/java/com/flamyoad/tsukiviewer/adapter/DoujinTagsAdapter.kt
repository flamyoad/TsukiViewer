package com.flamyoad.tsukiviewer.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.ui.search.SearchActivity
import com.flamyoad.tsukiviewer.ui.search.SearchResultActivity
import kotlinx.android.synthetic.main.doujin_tags_item.view.*

class DoujinTagsAdapter: RecyclerView.Adapter<DoujinTagsAdapter.DoujinTagHolder>() {

    private var tagList: List<Tag> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoujinTagHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.doujin_tags_item, parent, false)

        val holder = DoujinTagHolder(layout)

        layout.setOnClickListener {
            val tag = tagList[holder.adapterPosition]
            
            val context = parent.context
            
            val intent = Intent(context, SearchResultActivity::class.java)
            intent.putExtra(SearchActivity.SEARCH_TAGS, tag.name)

            context.startActivity(intent)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    override fun onBindViewHolder(holder: DoujinTagHolder, position: Int) {
        holder.bind(tagList[holder.adapterPosition])
    }

    fun setList(list: List<Tag>) {
        tagList = list
        notifyDataSetChanged()
    }

    inner class DoujinTagHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtName: TextView = itemView.findViewById(R.id.txtName)
        private val txtCount: TextView = itemView.findViewById(R.id.txtCount)

        fun bind(tag: Tag) {
            txtName.text = tag.name
            txtCount.text = tag.count.toString()
        }
    }
}