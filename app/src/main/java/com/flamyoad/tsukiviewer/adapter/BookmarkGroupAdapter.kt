package com.flamyoad.tsukiviewer.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.core.model.BookmarkGroup
import de.hdodenhof.circleimageview.CircleImageView

private const val BTN_ADD_NEW_GROUP = 0
private const val GROUP_ITEM = 1

class BookmarkGroupAdapter(
    private val onGroupClick: (BookmarkGroup) -> Unit,
    private val showNewGroupDialog: () -> Unit
) :
    ListAdapter<BookmarkGroup, RecyclerView.ViewHolder>(BookmarkDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            BTN_ADD_NEW_GROUP -> {
                val layout = LayoutInflater.from(parent.context)
                    .inflate(R.layout.bookmark_group_add_new, parent, false)
                val holder = AddButtonHolder(layout)

                layout.setOnClickListener {
                    showNewGroupDialog()
                }
                return holder
            }

            GROUP_ITEM -> {
                val layout = LayoutInflater.from(parent.context)
                    .inflate(R.layout.bookmark_group_item, parent, false)
                val holder = GroupViewHolder(layout)

                layout.setOnClickListener {
                    val group = getItem(holder.bindingAdapterPosition)
                    onGroupClick(group)
                }
                return holder
            }

            else -> throw IllegalArgumentException("Invalid RecyclerView item type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == currentList.size) {
            return
        }
        (holder as GroupViewHolder).bindTo(getItem(position))
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == currentList.size) {
            BTN_ADD_NEW_GROUP
        } else {
            GROUP_ITEM
        }
    }

    override fun getItemId(position: Int): Long {
        if (position == currentList.size) { // itemId of the Add Button
            return -1
        }
        return getItem(position).name.hashCode().toLong()
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail: CircleImageView = itemView.findViewById(R.id.circleImageView)
        private val txtGroupName: TextView = itemView.findViewById(R.id.txtGroupName)
        private val txtItemCount: TextView = itemView.findViewById(R.id.txtItemCount)
        private val txtPlaceHolder: TextView = itemView.findViewById(R.id.txtPlaceHolder)

        fun bindTo(group: BookmarkGroup) {
            txtGroupName.text = group.name
            txtItemCount.text = "( " + group.totalItems.toString() + " )"
            txtPlaceHolder.text = group.name.firstOrNull()?.toUpperCase().toString()

            if (group.pic != Uri.EMPTY) {
                Glide.with(itemView)
                    .load(group.pic)
                    .dontAnimate()
                    .into(thumbnail)
            } else {
                txtPlaceHolder.visibility = View.VISIBLE
            }

        }
    }

    inner class AddButtonHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}

class BookmarkDiffUtil : DiffUtil.ItemCallback<BookmarkGroup>() {
    override fun areItemsTheSame(oldItem: BookmarkGroup, newItem: BookmarkGroup): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: BookmarkGroup, newItem: BookmarkGroup): Boolean {
        return oldItem.lastDate == newItem.lastDate
                && oldItem.totalItems == newItem.totalItems
                && oldItem.pic == newItem.pic
    }

}