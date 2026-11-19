package com.flamyoad.tsukiviewer.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.reader.ReaderActivity
import com.flamyoad.tsukiviewer.ui.reader.ReaderPageImageView
import java.io.File

class ReaderImageAdapter(
    private val onPageReady: (SubsamplingScaleImageView) -> Unit,
    private val onPageError: (SubsamplingScaleImageView) -> Unit
) :
    RecyclerView.Adapter<ReaderImageAdapter.ImageViewHolder>() {

    private val visibleBounds = Rect()

    fun updatePlayback(recyclerView: RecyclerView, resumed: Boolean) {
        for (index in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(index)
            val holder = recyclerView.getChildViewHolder(child) as ImageViewHolder
            holder.updatePlayback(resumed, visibleBounds)
        }
    }

    override fun onViewDetachedFromWindow(holder: ImageViewHolder) {
        holder.stopAnimation()
        super.onViewDetachedFromWindow(holder)
    }

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
        private val photoView: ReaderPageImageView = itemView.findViewById(R.id.photoView)
        private val progressBarContainer: FrameLayout = itemView.findViewById(R.id.progressBarContainer)

        private var isGif = false
        private var playbackVisible = false

        fun bind(file: File) {
            playbackVisible = false
            isGif = file.extension.equals("gif", ignoreCase = true)
            photoView.transitionName = ReaderActivity.pageTransitionName(file)
            progressBarContainer.isVisible = true
            photoView.setOnImageEventListener(object :
                SubsamplingScaleImageView.DefaultOnImageEventListener() {
                override fun onReady() {
                    super.onReady()
                    progressBarContainer.isVisible = false
                    onPageReady(photoView)
                }

                override fun onImageLoadError(e: Exception) {
                    progressBarContainer.isVisible = false
                    onPageError(photoView)
                }
            })
            photoView.setPage(file)
        }

        fun updatePlayback(resumed: Boolean, visibleBounds: Rect) {
            val visible = resumed && isGif && photoView.isShown &&
                photoView.getGlobalVisibleRect(visibleBounds)
            if (visible == playbackVisible) return
            if (visible) {
                playbackVisible = true
                photoView.startAnimation()
            } else {
                stopAnimation()
            }
        }

        fun stopAnimation() {
            if (!playbackVisible) return
            playbackVisible = false
            photoView.stopAnimation()
        }

        fun recycle() {
            stopAnimation()
            photoView.transitionName = null
            photoView.setOnImageEventListener(null)
            photoView.recycle()
        }
    }

}