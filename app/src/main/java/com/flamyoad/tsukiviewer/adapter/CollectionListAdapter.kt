package com.flamyoad.tsukiviewer.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.Collection
import com.flamyoad.tsukiviewer.model.CollectionCriteria
import com.flamyoad.tsukiviewer.model.CollectionWithCriterias
import com.flamyoad.tsukiviewer.ui.home.collections.CollectionFragment
import com.flamyoad.tsukiviewer.ui.home.collections.DialogTagPicker.Mode
import com.flamyoad.tsukiviewer.ui.home.collections.doujins.CollectionDoujinsActivity
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import java.io.File

class CollectionListAdapter(
    private val onEditCollection: (Collection) -> Unit,
    private val onRemoveCollection: (Collection) -> Unit,
    private val onShowCollectionInfo: (Collection) -> Unit,
    private val viewType: Int
) : ListAdapter<CollectionWithCriterias, RecyclerView.ViewHolder>(CollectionDiffUtil()) {

    private val includedTagsViewPool = RecyclerView.RecycledViewPool()
    private val excludedTagsViewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = when (viewType) {
            LIST -> {
                val layout = LayoutInflater.from(parent.context)
                    .inflate(R.layout.collection_list_item_vertical, parent, false)
                ListItemViewHolder(layout)
            }
            GRID -> {
                val layout = LayoutInflater.from(parent.context)
                    .inflate(R.layout.collection_list_item_grid, parent, false)
                val holder = GridItemViewHolder(layout)

                val btnCollectionInfo: ImageButton = layout.findViewById(R.id.btnCollectionInfo)
                btnCollectionInfo.setOnClickListener {
                    val item = getItem(holder.bindingAdapterPosition)
                    onShowCollectionInfo(item.collection)
                }
                holder
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }

        holder.itemView.setOnClickListener {
            val item = getItem(holder.bindingAdapterPosition)
            val context = parent.context
            val intent = Intent(context, CollectionDoujinsActivity::class.java).apply {
                putExtra(CollectionFragment.COLLECTION_ID, item.collection.id)
                putExtra(CollectionFragment.COLLECTION_NAME, item.collection.name)
                putExtra(CollectionFragment.COLLECTION_CRITERIAS, item.getCriteriaNames())
            }
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            it.showContextMenu()
        }

        holder.itemView.setOnCreateContextMenuListener { contextMenu, view, contextMenuInfo ->
            val item = getItem(holder.bindingAdapterPosition)

            contextMenu.add("View Collection Criterias").setOnMenuItemClickListener {
                onShowCollectionInfo(item.collection)
                return@setOnMenuItemClickListener true
            }
            contextMenu.add("Edit Collection").setOnMenuItemClickListener {
                onEditCollection(item.collection)
                return@setOnMenuItemClickListener true
            }
            contextMenu.add("Delete Collection").setOnMenuItemClickListener {
                onRemoveCollection(item.collection)
                return@setOnMenuItemClickListener true
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (viewType) {
            LIST -> (holder as ListItemViewHolder).bind(getItem(position))
            GRID -> (holder as GridItemViewHolder).bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun getItemId(position: Int): Long {
        if (position == RecyclerView.NO_POSITION) {
            return -1
        }
        return getItem(position).collection.id ?: -1
    }

    inner class ListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtCollectionName: TextView = itemView.findViewById(R.id.txtCollectionName)
        private val txtTitles: TextView = itemView.findViewById(R.id.txtTitles)
        private val lblTitles: TextView = itemView.findViewById(R.id.lblTitles)
        private val coverImage: ImageView = itemView.findViewById(R.id.coverImage)
        private val imagePlaceHolder: SpinKitView = itemView.findViewById(R.id.imagePlaceholder)
        private val listIncludedTags: RecyclerView = itemView.findViewById(R.id.listIncludedTags)
        private val listExcludedTags: RecyclerView = itemView.findViewById(R.id.listExcludedTags)
        private val lblMinimumPages: TextView = itemView.findViewById(R.id.lblMinimumPages)
        private val lblMaximumPages: TextView = itemView.findViewById(R.id.lblMaximumPages)
        private val txtMinimumPages: TextView = itemView.findViewById(R.id.txtMinimumPages)
        private val txtMaximumPages: TextView = itemView.findViewById(R.id.txtMaximumPages)
        private val btnContextMenu: ImageButton = itemView.findViewById(R.id.btnContextMenu)

        fun bind(item: CollectionWithCriterias) {
            txtCollectionName.text = item.collection.name

            val titleFilters = item.criteriaList.filter { c -> c.type == CollectionCriteria.TITLE }
                .joinToString(", ") { c -> c.value }

            if (titleFilters.isBlank()) {
                txtTitles.visibility = View.GONE
                lblTitles.visibility = View.GONE
            } else {
                txtTitles.visibility = View.VISIBLE
                lblTitles.visibility = View.VISIBLE
                txtTitles.text = titleFilters
            }

            item.collection.minNumPages.let {
                if (it == Int.MIN_VALUE) {
                    lblMinimumPages.visibility = View.GONE
                    txtMinimumPages.visibility = View.GONE
                } else {
                    lblMinimumPages.visibility = View.VISIBLE
                    txtMinimumPages.visibility = View.VISIBLE
                    txtMinimumPages.text = it.toString()
                }
            }

            item.collection.maxNumPages.let {
                if (it == Int.MAX_VALUE) {
                    lblMaximumPages.visibility = View.GONE
                    txtMaximumPages.visibility = View.GONE
                } else {
                    lblMaximumPages.visibility = View.VISIBLE
                    txtMaximumPages.visibility = View.VISIBLE
                    txtMaximumPages.text = it.toString()
                }
            }

            val thumbnail = item.collection.coverPhoto
            if (thumbnail != File("")) {
                Glide.with(itemView.context)
                    .load(thumbnail)
                    .into(coverImage)
                coverImage.visibility = View.VISIBLE
                imagePlaceHolder.visibility = View.INVISIBLE
            } else {
                coverImage.visibility = View.INVISIBLE
                imagePlaceHolder.visibility = View.VISIBLE
            }

            val includedTags =
                item.criteriaList.filter { criteria -> criteria.type == CollectionCriteria.INCLUDED_TAGS }
            val excludedTags =
                item.criteriaList.filter { criteria -> criteria.type == CollectionCriteria.EXCLUDED_TAGS }

            initIncludedTags(includedTags, itemView.context)
            initExcludedTags(excludedTags, itemView.context)

            btnContextMenu.setOnClickListener {
                itemView.showContextMenu()
            }
        }

        fun initIncludedTags(list: List<CollectionCriteria>, context: Context) {
            val flexLayoutManager = FlexboxLayoutManager(context)
            flexLayoutManager.apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }

            listIncludedTags.apply {
                layoutManager = flexLayoutManager
                adapter = CollectionListCriteriasAdapter(list, Mode.Inclusive)
                setRecycledViewPool(includedTagsViewPool)
            }

        }

        fun initExcludedTags(list: List<CollectionCriteria>, context: Context) {
            val flexLayoutManager = FlexboxLayoutManager(context)
            flexLayoutManager.apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }

            listExcludedTags.apply {
                layoutManager = flexLayoutManager
                adapter = CollectionListCriteriasAdapter(list, Mode.Exclusive)
                setRecycledViewPool(excludedTagsViewPool)
            }
        }
    }

    inner class GridItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgThumbnail: ImageView = itemView.findViewById(R.id.imgThumbnail)
        private val txtCollectionName: TextView = itemView.findViewById(R.id.txtCollectionName)
        private val imagePlaceHolder: SpinKitView = itemView.findViewById(R.id.imagePlaceholder)

        fun bind(item: CollectionWithCriterias) {
            txtCollectionName.text = item.collection.name

            val thumbnail = item.collection.coverPhoto
            if (thumbnail != File("")) {
                Glide.with(itemView.context)
                    .load(thumbnail)
                    .into(imgThumbnail)
                imgThumbnail.visibility = View.VISIBLE
                imagePlaceHolder.visibility = View.INVISIBLE
            } else {
                imgThumbnail.visibility = View.INVISIBLE
                imagePlaceHolder.visibility = View.VISIBLE
            }
        }
    }

//    override fun onChange(position: Int): CharSequence {
//        return getItem(position).collection.name
//    }

    companion object {
        const val LIST = 1
        const val GRID = 2
    }
}

class CollectionDiffUtil : DiffUtil.ItemCallback<CollectionWithCriterias>() {
    override fun areItemsTheSame(
        oldItem: CollectionWithCriterias,
        newItem: CollectionWithCriterias
    ): Boolean {
        return oldItem.collection.id == newItem.collection.id
    }

    override fun areContentsTheSame(
        oldItem: CollectionWithCriterias,
        newItem: CollectionWithCriterias
    ): Boolean {
        val collectionsAreEqual = oldItem.collection == newItem.collection
        val criteriasAreEqual = oldItem.criteriaList == newItem.criteriaList

        return collectionsAreEqual && criteriasAreEqual
    }

}