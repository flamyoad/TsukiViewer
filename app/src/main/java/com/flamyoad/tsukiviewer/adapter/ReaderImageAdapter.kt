package com.flamyoad.tsukiviewer.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
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

        holder.setProgressbarHeight(ViewGroup.LayoutParams.MATCH_PARENT, recyclerViewHeight)

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
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun bind(file: File) {
            photoView.setImage(ImageSource.uri(Uri.fromFile(file)))
            progressBar.visibility = View.GONE
        }

        fun recycle() {
            photoView.recycle()
            setProgressbarHeight(ViewGroup.LayoutParams.MATCH_PARENT, recyclerViewHeight)
        }

        fun setProgressbarHeight(width: Int, height: Int) {
            progressBar.layoutParams = FrameLayout.LayoutParams(width, height)
        }
    }

}