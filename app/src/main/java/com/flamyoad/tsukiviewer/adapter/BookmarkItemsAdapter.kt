package com.flamyoad.tsukiviewer.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.BookmarkItem
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.ui.doujinpage.DoujinDetailsActivity
import com.flamyoad.tsukiviewer.ActionModeListener
import com.flamyoad.tsukiviewer.model.ViewMode
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller

class BookmarkItemsAdapter(
    private val actionListener: ActionModeListener<BookmarkItem>,
    var actionModeEnabled: Boolean

) : ListAdapter<BookmarkItem, BookmarkItemsAdapter.BookmarkViewHolder>(BookmarkDiffCallback()) {

    private var viewMode: ViewMode = ViewMode.NORMAL_GRID

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val layout = when (viewType) {
            ViewMode.NORMAL_GRID.toInt() -> inflater.inflate(R.layout.doujin_list_item_grid, parent, false)
            ViewMode.SCALED.toInt() -> inflater.inflate(R.layout.doujin_list_item_scaled, parent, false)
            ViewMode.MINI_GRID.toInt() -> inflater.inflate(R.layout.doujin_list_item_grid, parent, false)
            else -> throw IllegalArgumentException("Illegal view type")
        }

        val coverImage: ImageView = layout.findViewById(R.id.imgCover)

        val holder = BookmarkViewHolder(layout)

        layout.setOnClickListener {
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

        layout.setOnLongClickListener {
            if (!actionModeEnabled) {
                val zoomIn = AnimationUtils.loadAnimation(it.context, R.anim.doujin_img_zoom_in)
                val zoomOut = AnimationUtils.loadAnimation(it.context, R.anim.doujin_img_zoom_out)

                coverImage.startAnimation(zoomIn)

                actionListener.startActionMode()

                coverImage.startAnimation(zoomOut)
            }

            val itemIndex = holder.adapterPosition
            val item = getItem(itemIndex)

            actionListener.onMultiSelectionClick(item)
            return@setOnLongClickListener true
        }

        return holder
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).id ?: -1
    }

    override fun getItemViewType(position: Int): Int {
        return viewMode.toInt()
    }

    fun setViewMode(mode: ViewMode) {
        this.viewMode = mode
    }

    fun getViewMode() = viewMode

    inner class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImg: ImageView = itemView.findViewById(R.id.imgCover)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitleEng)
        private val txtPageNumber: TextView = itemView.findViewById(R.id.txtPageNumber)
        private val multiSelectIndicator: ImageView =
            itemView.findViewById(R.id.multiSelectIndicator)

        fun bind(item: BookmarkItem) {
            if (item.isSelected) {
                multiSelectIndicator.setImageResource(R.drawable.ic_check_blue_custom)
            } else {
                if (multiSelectIndicator.drawable != null) {
                    multiSelectIndicator.setImageDrawable(null) // Clear selected icon if exists previously
                }
            }

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
}

class BookmarkDiffCallback : DiffUtil.ItemCallback<BookmarkItem>() {
    override fun areItemsTheSame(oldItem: BookmarkItem, newItem: BookmarkItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BookmarkItem, newItem: BookmarkItem): Boolean {
        val status = oldItem == newItem
                && oldItem.isSelected == newItem.isSelected

        return status
    }
}
