package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flamyoad.tsukiviewer.R
import com.github.chrisbanes.photoview.PhotoView
import java.io.File

class ReaderImageAdapter : RecyclerView.Adapter<ReaderImageAdapter.ImageViewHolder>() {

    private var imageList: List<File> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.reader_image_item, parent, false)

        val holder = ImageViewHolder(layout)

        return holder
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    fun setList(list: List<File>) {
        imageList = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoView: PhotoView = itemView.findViewById(R.id.photoView)

        fun bind(file: File) {
            Glide.with(itemView)
                .load(file.toUri())
                .into(photoView)
        }
    }

}