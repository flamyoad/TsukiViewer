package com.flamyoad.tsukiviewer.adapter

import android.content.Context
import android.content.Intent
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
    private val onRemoveCollection: (Collection) -> Unit
) :
    RecyclerViewFastScroller.OnPopupTextUpdate,
    ListAdapter<CollectionWithCriterias, CollectionListAdapter.CollectionViewHolder>(CollectionDiffUtil()) {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.collection_list_item, parent, false)
        val holder = CollectionViewHolder(layout)

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

        holder.itemView.setOnCreateContextMenuListener { contextMenu, view, contextMenuInfo ->
            val item = getItem(holder.bindingAdapterPosition)

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

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtCollectionName: TextView = itemView.findViewById(R.id.txtCollectionName)
        private val txtTitles: TextView = itemView.findViewById(R.id.txtTitles)
        private val lblTitles: TextView = itemView.findViewById(R.id.lblTitles)
        private val coverImage: ImageView = itemView.findViewById(R.id.coverImage)
        private val imagePlaceHolder: SpinKitView = itemView.findViewById(R.id.imagePlaceholder)
        private val listIncludedTags: RecyclerView = itemView.findViewById(R.id.listIncludedTags)
        private val listExcludedTags: RecyclerView = itemView.findViewById(R.id.listExcludedTags)
        private val btnContextMenu: ImageButton = itemView.findViewById(R.id.btnContextMenu)

        fun bind(item: CollectionWithCriterias) {
            txtCollectionName.text = item.collection.name

            val titleFilters = item.criteriaList.filter { c -> c.type == CollectionCriteria.TITLE }
                .joinToString(", ") { c -> c.value }

            if (titleFilters.isBlank()) {
                txtTitles.visibility = View.GONE
                lblTitles.visibility = View.GONE
            } else {
                txtTitles.text = titleFilters
            }

            val thumbnail = item.collection.coverPhoto
            if (coverImage != File("")) {
                Glide.with(itemView.context)
                    .load(thumbnail)
                    .into(coverImage)
                imagePlaceHolder.visibility = View.GONE
            } else {
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
                setRecycledViewPool(viewPool)
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
                setRecycledViewPool(viewPool)
            }
        }
    }

    override fun onChange(position: Int): CharSequence {
        return getItem(position).collection.name
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