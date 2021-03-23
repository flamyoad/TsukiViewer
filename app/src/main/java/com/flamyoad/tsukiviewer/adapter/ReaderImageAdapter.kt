package com.flamyoad.tsukiviewer.adapter

import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.flamyoad.tsukiviewer.R
import com.github.chrisbanes.photoview.PhotoView
import java.io.File

class ReaderImageAdapter
    : RecyclerView.Adapter<ReaderImageAdapter.ImageViewHolder>() {

    private var imageList: List<File> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.reader_image_item_wrap_height, parent, false)

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

    override fun onViewRecycled(holder: ImageViewHolder) {
        super.onViewRecycled(holder)
        holder.recycleBitmap()
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoView: SubsamplingScaleImageView = itemView.findViewById(R.id.photoView)

        fun bind(file: File) {
            photoView.setImage(ImageSource.uri(Uri.fromFile(file)))
        }

        fun recycleBitmap() {
            photoView.recycle()
        }
    }

}