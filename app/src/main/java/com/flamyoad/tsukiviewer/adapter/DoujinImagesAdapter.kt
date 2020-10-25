package com.flamyoad.tsukiviewer.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.reader.ReaderActivity
import com.flamyoad.tsukiviewer.ui.settings.preferences.MainPreferences
import com.flamyoad.tsukiviewer.utils.TimeUtils
import org.w3c.dom.Text
import java.io.File


class DoujinImagesAdapter(
    private val itemType: ItemType,
    private val currentDir: String,
    private val startActivityForResult: (Intent) -> Unit // Function is passed from Activity to here since startActivityForResult() can only be called from Activity
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val POSITION_BEFORE_OPENING_READER =
            "DoujinImagesAdapter.POSITION_BEFORE_OPENING_READER"
        const val POSITION_AFTER_EXITING_READER =
            "DoujinImagesAdapter.POSITION_AFTER_EXITING_READER"
        const val DIRECTORY_PATH = "DoujinImagesAdapter.DIRECTORY_PATH"
    }

    private var imageList: List<File> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val layout = when (itemType) {
            ItemType.Grid -> layoutInflater.inflate(R.layout.image_grid_item, parent, false)
            ItemType.Scaled -> layoutInflater.inflate(R.layout.image_scaled_item, parent, false)
            ItemType.Row -> layoutInflater.inflate(R.layout.image_row_item, parent, false)
            ItemType.List -> layoutInflater.inflate(R.layout.image_list_item, parent, false)
        }

        val holder = when (itemType) {
            ItemType.List -> DetailedImageViewHolder(layout)
            else -> ImageViewHolder(layout)
        }

        layout.setOnClickListener {
            val image = imageList[holder.adapterPosition]

            val context = parent.context

            val prefs = PreferenceManager.getDefaultSharedPreferences(context)

            val shouldUseExternalApps =
                prefs.getBoolean(MainPreferences.USE_EXTERNAL_GALLERY, false)

            if (shouldUseExternalApps) {
                launchExternalGallery(context, image)
            } else {
                val intent = Intent(context, ReaderActivity::class.java)
                intent.putExtra(POSITION_BEFORE_OPENING_READER, holder.adapterPosition)
                intent.putExtra(DIRECTORY_PATH, currentDir)

                startActivityForResult(intent)
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image = imageList[holder.adapterPosition]
        when (itemType) {
            ItemType.List -> (holder as DetailedImageViewHolder).bind(image)
            else -> (holder as ImageViewHolder).bind(image)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun getItemId(position: Int): Long {
        return imageList[position].hashCode().toLong()
    }

    fun setList(list: List<File>) {
        imageList = list
        notifyDataSetChanged()
    }

    private fun launchExternalGallery(context: Context, file: File) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val packageName = prefs.getString(MainPreferences.EXTERNAL_GALLERY_PKG_NAME, "")

        try {
            val intent = Intent()
            val uri = Uri.fromFile(file)

            intent.apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                action = Intent.ACTION_VIEW
                setPackage(packageName)
                setDataAndType(uri, "image/*")
            }

            context.startActivity(intent)

        } catch (e: Exception) {
            Toast.makeText(context, "Can't open the external application", Toast.LENGTH_SHORT)
                .show()
        }
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(file: File) {
            Glide.with(itemView.context)
                .load(file.toUri())
                .transition(withCrossFade())
                .into(imageView)
        }
    }

    inner class DetailedImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)

        fun bind(file: File) {
            Glide.with(itemView.context)
                .load(file.toUri())
                .transition(withCrossFade())
                .into(imageView)

            txtName.text = file.name
            txtDate.text = TimeUtils.getReadableDate(file.lastModified())
        }
    }

    enum class ItemType {
        Grid, Scaled, Row, List
    }
}