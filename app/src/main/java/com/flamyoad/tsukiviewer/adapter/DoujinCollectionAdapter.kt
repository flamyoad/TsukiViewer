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
import com.flamyoad.tsukiviewer.ui.home.collection.ToggleHeaderListener
import com.flamyoad.tsukiviewer.utils.CollectionDiffCallback
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller

class DoujinCollectionAdapter(
    private val actionListener: ActionModeListener,
    private val headerListener: ToggleHeaderListener,
    var actionModeEnabled: Boolean

) : ListAdapter<CollectionItem, RecyclerView.ViewHolder>(CollectionDiffCallback()),
    RecyclerViewFastScroller.OnPopupTextUpdate {

    companion object {
        const val HEADER_TYPE = 0
        const val ITEM_TYPE = 1
        const val EMPTY = 2
    }

    var onLongClickItem: CollectionItem? = null
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        when (viewType) {
            HEADER_TYPE -> {
                val view = layoutInflater.inflate(R.layout.collection_list_header, parent, false)
                val holder = HeaderViewHolder(view)

                view.setOnClickListener {
                    val header = getItem(holder.adapterPosition)
                    headerListener.toggleHeader(header, holder.adapterPosition)
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
                            actionListener.onMultiSelectionClick(item)
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
                    actionListener.startActionMode()

                    val itemIndex = holder.adapterPosition
                    val item = getItem(itemIndex)

                    actionListener.onMultiSelectionClick(item)
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

    override fun getItemId(position: Int): Long {
        val item = getItem(position)
        return item.id ?: -1
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item.isHeader) {
            true -> HEADER_TYPE
            false -> ITEM_TYPE
        }
    }

    override fun onChange(position: Int): CharSequence {
        return getItem(position).collectionName
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