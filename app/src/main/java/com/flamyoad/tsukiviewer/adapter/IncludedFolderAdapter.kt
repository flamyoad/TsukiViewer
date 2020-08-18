package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.IncludedFolder
import com.flamyoad.tsukiviewer.ui.settings.includedfolders.RemoveFolderListener
import java.lang.IllegalArgumentException

class IncludedFolderAdapter(private val listener: RemoveFolderListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val FOLDER_ITEM = 0
    private val EMPTY_INDICATOR = 1

    private var folderList = emptyList<IncludedFolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            FOLDER_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.included_folders_item, parent, false)

                val holder = FolderViewHolder(view)

                val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)
                btnDelete.setOnClickListener {
                    val folder = folderList[holder.adapterPosition]
                    listener.deleteFolder(folder)
                }

                return holder
            }

            EMPTY_INDICATOR -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.loading_indicator, parent, false)

                return EmptyViewHolder(view)
            }

            else ->  {
                throw IllegalArgumentException("Illegal view type")
            }
        }
    }

    override fun getItemCount(): Int {
        return if (folderList.isNotEmpty()) {
            folderList.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (folderList.isNullOrEmpty()) {
            EMPTY_INDICATOR
        } else {
            FOLDER_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            FOLDER_ITEM -> {
                val folderHolder = holder as FolderViewHolder
                folderHolder.bind(folderList[holder.adapterPosition])
            }
        }
    }

    fun setList(list: List<IncludedFolder>) {
        folderList = list
        notifyDataSetChanged()
    }

    inner class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtDirectory: TextView = itemView.findViewById(R.id.txtDirectory)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(folder: IncludedFolder) {
            txtDirectory.text = folder.dir.canonicalPath
        }
    }
}