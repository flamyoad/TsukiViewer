package com.flamyoad.tsukiviewer.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flamyoad.tsukiviewer.R
import com.google.android.material.card.MaterialCardView
import java.io.File

class BottomThumbnailAdapter(
    private val listener: OnItemClickListener,
    private val sidebar: Boolean = false
)
    : RecyclerView.Adapter<BottomThumbnailAdapter.ThumbnailViewHolder>() {

    private var list: List<File> = emptyList()
    private var selectedPage = RecyclerView.NO_POSITION
    private object SelectionChanged

    fun selectPage(position: Int) {
        if (selectedPage == position) return
        val previousPage = selectedPage
        selectedPage = position
        if (previousPage in list.indices) notifyItemChanged(previousPage, SelectionChanged)
        if (position in list.indices) notifyItemChanged(position, SelectionChanged)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val layoutRes = if (sidebar) R.layout.reader_page_sidebar_item
            else R.layout.reader_bottom_thumbnails_item
        val layout = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)

        val holder = ThumbnailViewHolder(layout)

        layout.setOnClickListener {
            val position = holder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) listener.onThumbnailClick(position)
        }

        return holder
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        val image = list[position]
        holder.bind(image, position)
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty() && payloads.all { it === SelectionChanged }) {
            holder.bindSelection(position == selectedPage)
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(list: List<File>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class ThumbnailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val txtPageNumber: TextView = itemView.findViewById(R.id.txtPageNumber)

        fun bind(file: File, adapterPosition: Int) {
            val glide = Glide.with(itemView)
            if (file.extension.equals("gif", ignoreCase = true)) {
                glide.asBitmap().load(file.toUri()).into(imageView)
            } else {
                glide.load(file.toUri()).into(imageView)
            }

            val page = adapterPosition + 1
            txtPageNumber.text = page.toString()
            val card = itemView as? MaterialCardView ?: return
            card.contentDescription = card.resources.getString(R.string.reader_page_thumbnail, page)
            bindSelection(adapterPosition == selectedPage)
        }

        fun bindSelection(selected: Boolean) {
            val card = itemView as? MaterialCardView ?: return
            card.isSelected = selected
            card.strokeWidth = if (selected) (2 * card.resources.displayMetrics.density).toInt() else 0
            val accent = ContextCompat.getColor(card.context, R.color.wine_red)
            txtPageNumber.setBackgroundColor(if (selected) accent else Color.argb(153, 0, 0, 0))
        }
    }

    interface OnItemClickListener {
        fun onThumbnailClick(adapterPosition: Int)
    }

}
