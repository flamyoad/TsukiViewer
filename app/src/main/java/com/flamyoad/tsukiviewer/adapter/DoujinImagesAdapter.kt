package com.flamyoad.tsukiviewer.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.reader.ReaderActivity
import java.io.File

class DoujinImagesAdapter(private val itemType: ItemType,
                          private val currentDir: String) :
    RecyclerView.Adapter<DoujinImagesAdapter.ImageViewHolder>() {

    companion object {
        @JvmStatic val ADAPTER_POSITION = "DoujinImagesAdapter.ADAPTER_POSITION"
        @JvmStatic val DIRECTORY_PATH = "DoujinImagesAdapter.DIRECTORY_PATH"
    }

    private var imageList: List<File> = emptyList()

    var count = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val xml = if (itemType == ItemType.Grid) {
            R.layout.image_grid_item
        } else {
            R.layout.image_list_item
        }

        val layout = LayoutInflater.from(parent.context)
            .inflate(xml, parent, false)

        val holder = ImageViewHolder(layout)

        layout.setOnClickListener {
            val image = imageList[holder.adapterPosition]

            val context = parent.context

            val intent = Intent(context, ReaderActivity::class.java)

            intent.putExtra(ADAPTER_POSITION, holder.adapterPosition)
            intent.putExtra(DIRECTORY_PATH, currentDir)

            context.startActivity(intent)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[holder.adapterPosition])
        Log.d("bind", "Binded for $count times")
        count++
    }

    fun setList(list: List<File>) {
        imageList = list
        notifyDataSetChanged()
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(file: File) {
            Glide.with(itemView.context)
                .load(file.toUri())
                .thumbnail(0.5f)
                .into(imageView)
        }
    }

    enum class ItemType {
        Grid, List
    }
}