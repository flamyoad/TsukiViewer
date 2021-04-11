package com.flamyoad.tsukiviewer.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.flamyoad.tsukiviewer.R
import java.io.File

class ReaderImageAdapter :
    RecyclerView.Adapter<ReaderImageAdapter.ImageViewHolder>() {

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
        holder.recycle()
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val photoView: SubsamplingScaleImageView = itemView.findViewById(R.id.photoView)
        private val progressBarContainer: FrameLayout = itemView.findViewById(R.id.progressBarContainer)

        fun bind(file: File) {
            photoView.setOnImageEventListener(object :
                SubsamplingScaleImageView.DefaultOnImageEventListener() {
                override fun onReady() {
                    super.onReady()
                    progressBarContainer.isVisible = false
                }
            })
            photoView.setImage(ImageSource.uri(Uri.fromFile(file)))
        }

        fun recycle() {
            photoView.recycle()
        }
    }

}