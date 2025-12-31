package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.core.model.BookmarkGroup
import com.flamyoad.tsukiviewer.ui.doujinpage.BookmarkGroupDialogListener

class CollectionPickerAdapter(private val listener: BookmarkGroupDialogListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ADD_NEW_BUTTON = 1
    private val COLLECTION_ITEM = 2

    private var list: List<BookmarkGroup> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ADD_NEW_BUTTON -> {
                val view = inflater.inflate(R.layout.dialog_select_favourite_addnew, parent, false)
                val holder = ButtonAddNewItem(view)

                holder.itemView.setOnClickListener {
                    listener.onAddBookmarkGroup()
                }
                return holder
            }

            COLLECTION_ITEM -> {
                val view =
                    inflater.inflate(R.layout.dialog_select_favourite_listitem, parent, false)
                val holder = CollectionViewHolder(view)

                val holderCheckbox = holder.itemView.findViewById<CheckBox>(R.id.checkBox)

                holder.itemView.setOnClickListener {
                    val bookmarkGroup = list[holder.bindingAdapterPosition - 1]
                    when (bookmarkGroup.isTicked) {
                        true -> untickBookmarkGroup(bookmarkGroup)
                        false -> tickBookmarkGroup(bookmarkGroup)
                    }
                }

                holderCheckbox.setOnClickListener {
                    val bookmarkGroup = list[holder.bindingAdapterPosition - 1]
                    when (bookmarkGroup.isTicked) {
                        true -> untickBookmarkGroup(bookmarkGroup)
                        false -> tickBookmarkGroup(bookmarkGroup)
                    }
                }

                return holder
            }

            else -> {
                throw IllegalArgumentException("Wrong item type")
            }
        }
    }

    // First item - Add New Collection button
    // Rest of the list - DoujinCollection.kt items inside the ArrayList
    override fun getItemCount(): Int {
        return list.size + 1
    }

    // First item in RecyclerView is hardcoded to be the button for adding new items.
    // Hence, we need to minus 1 from position to prevent IndexOutOfBound exception
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            // new button
        } else {
            val realPosition = position - 1
            val collectionHolder = holder as CollectionViewHolder
            collectionHolder.bindTo(list[realPosition])
        }
    }

    // First item is the button for adding new items.
    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> ADD_NEW_BUTTON
            else -> COLLECTION_ITEM
        }
    }

    fun setList(newList: List<BookmarkGroup>) {
        list = newList
        notifyDataSetChanged()
    }

    private fun tickBookmarkGroup(group: BookmarkGroup) {
        listener.onBookmarkGroupTicked(group)
        val newList = list.map {
            if (it == group) {
                return@map it.copy(isTicked = true)
            } else {
                return@map it
            }
        }

        list = newList
        notifyDataSetChanged()
    }

    private fun untickBookmarkGroup(group: BookmarkGroup) {
        listener.onBookmarkGroupUnticked(group)
        val newList = list.map {
            if (it == group) {
                return@map it.copy(isTicked = false)
            } else {
                return@map it
            }
        }

        list = newList
        notifyDataSetChanged()
    }

    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtIcon: TextView = itemView.findViewById(R.id.txtIcon)
        private val txtCollectionName: TextView = itemView.findViewById(R.id.txtCollectionName)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bindTo(collection: BookmarkGroup) {
            val firstLetter = collection.name.first().toUpperCase()

            txtIcon.text = firstLetter.toString()
            txtCollectionName.text = collection.name
            checkBox.isChecked = collection.isTicked
        }

        fun toggleCheckbox() {
            checkBox.isChecked = !checkBox.isChecked
        }
    }

    inner class ButtonAddNewItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val layout: ConstraintLayout = itemView.findViewById(R.id.root)
    }
}