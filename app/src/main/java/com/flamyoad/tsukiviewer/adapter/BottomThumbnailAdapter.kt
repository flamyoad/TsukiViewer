package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flamyoad.tsukiviewer.R
import java.io.File

class BottomThumbnailAdapter(private val listener: OnItemClickListener)
    : RecyclerView.Adapter<BottomThumbnailAdapter.ThumbnailViewHolder>() {

    private var list: List<File> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.reader_bottom_thumbnails_item, parent, false)

        val holder = ThumbnailViewHolder(layout)

        layout.setOnClickListener {
            val position = holder.adapterPosition
            listener.onThumbnailClick(position)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        val image = list[position]
        holder.bind(image, holder.adapterPosition)
    }

    fun setList(list: List<File>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class ThumbnailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val txtPageNumber: TextView = itemView.findViewById(R.id.txtPageNumber)

        fun bind(file: File, adapterPosition: Int) {
            Glide.with(itemView)
                .load(file.toUri())
//                .sizeMultiplier(0.75f)
                .into(imageView)

            txtPageNumber.text = (adapterPosition + 1).toString()
        }
    }

    interface OnItemClickListener {
        fun onThumbnailClick(adapterPosition: Int)
    }
}