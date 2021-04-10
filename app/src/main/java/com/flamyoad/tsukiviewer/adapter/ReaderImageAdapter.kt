package com.flamyoad.tsukiviewer.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.flamyoad.tsukiviewer.R
import java.io.File

class ReaderImageAdapter(private val recyclerViewHeight: Int) :
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
        private val frame: FrameLayout = itemView.findViewById(R.id.frame)

        fun bind(file: File) {
            val progressContainer = createProgressBar(itemView.context)
            photoView.setOnImageEventListener(object: SubsamplingScaleImageView.DefaultOnImageEventListener() {
                override fun onReady() {
                    super.onReady()
                    progressContainer.isVisible = false
                }
            })
            photoView.setImage(ImageSource.uri(Uri.fromFile(file)))
        }

        private fun createProgressBar(context: Context): FrameLayout {
            val progressContainer = FrameLayout(context)
            frame.addView(progressContainer, ViewGroup.LayoutParams.MATCH_PARENT, recyclerViewHeight)
            val progress = ProgressBar(context).apply {
                val size = 72
                indeterminateTintList = ColorStateList.valueOf(Color.WHITE)
                layoutParams = FrameLayout.LayoutParams(size, size).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    setMargins(0, recyclerViewHeight / 4, 0, 0)
                }
            }
            progressContainer.addView(progress)
            return progressContainer
        }

        fun recycle() {
            photoView.recycle()
        }
    }

}