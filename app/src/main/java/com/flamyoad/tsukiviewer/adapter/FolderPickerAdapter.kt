package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.settings.folderpicker.FolderPickerListener
import com.flamyoad.tsukiviewer.utils.EmptyViewHolder
import java.io.File

class FolderPickerAdapter(private val listener: FolderPickerListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val FOLDER_ITEM = 0
    private val EMPTY_INDICATOR = 1

    private var list: List<File> = emptyList()

    private var currentDir: File? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            FOLDER_ITEM -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.folder_list_item, parent, false)

                val holder = FileViewHolder(view)

                view.setOnClickListener {
                    val dir = list[holder.adapterPosition]
                    listener.onFolderPick(dir)
                }

                return holder
            }

            EMPTY_INDICATOR -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.no_result_found, parent, false)

                return EmptyViewHolder(view)
            }

            else -> {
                throw IllegalArgumentException("Illegal view type")
            }
        }
    }

    override fun getItemCount(): Int {
        return if (list.isNotEmpty()) {
            list.size
        } else {
            1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list.isNullOrEmpty()) {
            EMPTY_INDICATOR
        } else {
            FOLDER_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            FOLDER_ITEM -> {
                val fileHolder = holder as FileViewHolder
                fileHolder.bind(list[holder.adapterPosition])
            }
        }
    }

    fun setCurrentDirectory(dir: File) {
        currentDir = dir
    }

    fun setList(list: List<File>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun clearList() {
        this.list = emptyList()
        notifyDataSetChanged()
    }

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtFolderName = itemView.findViewById<TextView>(R.id.txtDoujinName)

        fun bind(file: File) {
            if (currentDir!!.parentFile.name == file.name) {
                txtFolderName.text = ".."
            } else {
                txtFolderName.text = file.name
            }
        }
    }
}