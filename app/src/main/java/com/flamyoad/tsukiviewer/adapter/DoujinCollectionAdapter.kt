package com.flamyoad.tsukiviewer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.CollectionItem
import com.flamyoad.tsukiviewer.model.Doujin
import com.flamyoad.tsukiviewer.ui.doujinpage.DoujinDetailsActivity

class DoujinCollectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val HEADER_TYPE = 0
        const val ITEM_TYPE = 1
        const val EMPTY = 2
    }

    private var list: List<CollectionItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        when (viewType) {
            HEADER_TYPE -> {
                val view = layoutInflater.inflate(R.layout.collection_list_header, parent, false)
                val holder = HeaderViewHolder(view)

                view.setOnClickListener {

                }
                return holder
            }

            ITEM_TYPE -> {
                val view = layoutInflater.inflate(R.layout.doujin_list_item, parent, false)
                val holder = ItemViewHolder(view)

                view.setOnClickListener {
                    val position = holder.bindingAdapterPosition
                    if (position == RecyclerView.NO_POSITION) {
                        return@setOnClickListener
                    }

                    val currentDoujin = list[position].doujin
                    openDoujin(it.context, currentDoujin)
                }
                return holder
            }

            else -> {
                throw IllegalArgumentException("View type does not exist")
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder.itemViewType) {
            HEADER_TYPE -> (holder as HeaderViewHolder).bind(item)
            ITEM_TYPE -> (holder as ItemViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = list[position]
        return when (item.isHeader) {
            true -> HEADER_TYPE
            false -> ITEM_TYPE
        }
    }

    fun setList(newList: List<CollectionItem>) {
        list = newList
        notifyDataSetChanged()
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
        }
    }
}