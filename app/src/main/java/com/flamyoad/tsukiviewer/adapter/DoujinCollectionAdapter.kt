package com.flamyoad.tsukiviewer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.CollectionItem
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.ui.doujinpage.DoujinDetailsActivity
import com.flamyoad.tsukiviewer.ui.home.collection.ActionModeListener
import com.flamyoad.tsukiviewer.utils.CollectionDiffCallback

class DoujinCollectionAdapter(private val listener: ActionModeListener) :
    ListAdapter<CollectionItem, RecyclerView.ViewHolder>(CollectionDiffCallback()) {

    companion object {
        const val HEADER_TYPE = 0
        const val ITEM_TYPE = 1
        const val EMPTY = 2
    }

    private val itemsByHeader = hashMapOf<String, List<CollectionItem>>()

    var onLongClickItem: CollectionItem? = null
        private set

    private var actionModeEnabled: Boolean = false

    val selectedItems = mutableListOf<CollectionItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        when (viewType) {
            HEADER_TYPE -> {
                val view = layoutInflater.inflate(R.layout.collection_list_header, parent, false)
                val holder = HeaderViewHolder(view)

                view.setOnClickListener {
                    val header = getItem(holder.adapterPosition)
                    toggleHeader(header, holder.adapterPosition)
                }

                // https://stackoverflow.com/questions/49234423/full-screen-floating-context-menu-in-android-8-0-api-26
                view.setOnLongClickListener {
                    val header = getItem(holder.adapterPosition)
                    onLongClickItem = header

                    it.showContextMenu()

                    return@setOnLongClickListener true
                }

                return holder
            }

            ITEM_TYPE -> {
                val view = layoutInflater.inflate(R.layout.doujin_list_item, parent, false)
                val holder = ItemViewHolder(view)

                view.setOnClickListener {
                    val itemIndex = holder.adapterPosition
                    val item = getItem(itemIndex)

                    when (actionModeEnabled) {
                        true -> {
                            onMultiSelectionClick(item, itemIndex)
                        }

                        false -> {
                            val position = holder.bindingAdapterPosition
                            if (position == RecyclerView.NO_POSITION) {
                                return@setOnClickListener
                            }

                            val currentDoujin = item.doujin
                            openDoujin(it.context, currentDoujin)
                        }
                    }
                }

                view.setOnLongClickListener {
                    listener.openActionMode()

                    val itemIndex = holder.adapterPosition
                    val item = getItem(itemIndex)

                    onMultiSelectionClick(item, itemIndex)

                    return@setOnLongClickListener true
                }

                return holder
            }

            else -> {
                throw IllegalArgumentException("View type does not exist")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder.itemViewType) {
            HEADER_TYPE -> (holder as HeaderViewHolder).bind(item)
            ITEM_TYPE -> (holder as ItemViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.isHeader) {
            true -> HEADER_TYPE
            false -> ITEM_TYPE
        }
    }

    private fun openDoujin(context: Context, doujin: Doujin?) {
        if (doujin == null) {
            return
        }

        val intent = Intent(context, DoujinDetailsActivity::class.java)

        intent.putExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH, doujin.path.toString())
        intent.putExtra(LocalDoujinsAdapter.DOUJIN_NAME, doujin.title)

        context.startActivity(intent)
    }

    private fun toggleHeader(header: CollectionItem, headerPosition: Int) {
        when (header.isCollapsed) {
            true -> show(header.collectionName, headerPosition)
            false -> collapse(header.collectionName, headerPosition)
        }
        header.isCollapsed = !header.isCollapsed
    }

    private fun collapse(collectionName: String, headerPosition: Int) {
        val currentItems = currentList.toMutableList()

        val lastItem = currentItems.findLast { item -> item.collectionName == collectionName }

        val lastItemIndex = currentItems.indexOf(lastItem)

        // fromIndex (inclusive) and toIndex (exclusive)
        val collapsedItems = currentItems.subList(headerPosition + 1, lastItemIndex + 1).toList()

        itemsByHeader.put(collectionName, collapsedItems)

        currentItems.removeAll { item -> !item.isHeader && item.collectionName == collectionName }

        submitList(currentItems)
    }

    private fun show(collectionName: String, headerPosition: Int) {
        val collapsedItems = itemsByHeader[collectionName]

        if (collapsedItems != null) {
            val currentItems = currentList.toMutableList()
            currentItems.addAll(headerPosition + 1, collapsedItems)
            submitList(currentItems)
        }
    }

    fun setActionMode(status: Boolean) {
        actionModeEnabled = status

        if (!status) {
            selectedItems.clear()

            val currentList = currentList.toList()

            itemsByHeader.forEach { (header, items) ->
                for (item in items) {
                    item.isSelected = false
                }
            }

            for (item in currentList) {
                item.isSelected = false
            }

            submitList(currentList)

            // Removes all the circle indicator
            notifyItemRangeChanged(0, itemCount)
        }
    }

    private fun onMultiSelectionClick(item: CollectionItem, index: Int) {
        if (selectedItems.contains(item)) {
            selectedItems.remove(item)
            item.isSelected = false

        } else {
            selectedItems.add(item)
            item.isSelected = true
        }
        notifyItemChanged(index)
        listener.onItemCountChange(selectedItems.size)
    }

    fun getSelectedItemCount(): Int {
        return selectedItems.size
    }

    fun getSelectedItemNames(): Array<CharSequence> {
        return selectedItems
            .map { item -> item.doujin?.title ?: "" }
            .toTypedArray()
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtTitle: TextView = itemView.findViewById(R.id.header)

        fun bind(item: CollectionItem) {
            txtTitle.text = item.collectionName
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImg: ImageView = itemView.findViewById(R.id.imgCover)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitleEng)
        private val txtPageNumber: TextView = itemView.findViewById(R.id.txtPageNumber)
        private val multiSelectIndicator: ImageView =
            itemView.findViewById(R.id.multiSelectIndicator)

        fun bind(item: CollectionItem) {
            val doujin = item.doujin
            if (doujin != null) {
                Glide.with(itemView.context)
                    .load(doujin.pic)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .sizeMultiplier(0.75f)
                    .into(coverImg)

                txtTitle.text = doujin.title
                txtPageNumber.text = doujin.numberOfItems.toString()
            }

            when (item.isSelected) {
                true -> setIconVisibility(View.VISIBLE)
                false -> setIconVisibility(View.GONE)
            }
        }

        fun setIconVisibility(visibility: Int) {
            when (visibility) {
                View.VISIBLE -> multiSelectIndicator.visibility = visibility
                View.GONE -> multiSelectIndicator.visibility = visibility
                else -> return
            }
        }
    }
}