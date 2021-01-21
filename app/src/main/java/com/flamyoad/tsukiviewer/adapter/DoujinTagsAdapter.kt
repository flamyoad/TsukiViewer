package com.flamyoad.tsukiviewer.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.ui.search.SearchActivity
import com.flamyoad.tsukiviewer.ui.search.SearchResultActivity
import com.flamyoad.tsukiviewer.utils.ActivityStackUtils
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class DoujinTagsAdapter(private val useLargerView: Boolean,
                        private val saveActivityInfo: () -> Unit = {}) :
    RecyclerView.Adapter<DoujinTagsAdapter.DoujinTagHolder>(),
    FastScrollRecyclerView.SectionedAdapter {

    private var tagList: List<Tag> = emptyList()

    private var showDeleteDialog: ((Tag) -> Unit)? = null

    fun setListener(lambda: (Tag) -> Unit) = run { showDeleteDialog = lambda }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoujinTagHolder {
        val layoutId = when (useLargerView) {
            true -> R.layout.doujin_tags_item_larger
            false -> R.layout.doujin_tags_item
        }

        val layout = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)

        val holder = DoujinTagHolder(layout)

        layout.setOnClickListener {
            val tag = tagList[holder.adapterPosition]

            val context = parent.context

            val intent = Intent(context, SearchResultActivity::class.java).apply {
                putExtra(SearchActivity.SEARCH_TAGS, tag.name)
            }

            if (ActivityStackUtils.shouldStartWithClearTop(context)) {
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            saveActivityInfo()
            context.startActivity(intent)
        }

        layout.setOnLongClickListener {
            if (showDeleteDialog != null) {
                val tag = tagList[holder.adapterPosition]
                showDeleteDialog?.invoke(tag)
            }
            return@setOnLongClickListener true
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

    override fun getSectionName(position: Int): String {
        val item = tagList[position]
        return if (item.name.isBlank()) {
            ""
        } else {
            item.name.first().toString()
        }
    }
}