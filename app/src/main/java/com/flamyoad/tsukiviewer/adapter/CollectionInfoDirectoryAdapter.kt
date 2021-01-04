package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import java.io.File

class CollectionInfoDirectoryAdapter :
    RecyclerView.Adapter<CollectionInfoDirectoryAdapter.DirectoryViewHolder>() {

    private var dirList: List<File> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.collection_info_dir_item, null, false)
        val holder = DirectoryViewHolder(view)

        return holder
    }

    override fun getItemCount(): Int {
        return if (dirList.isEmpty()) {
            1
        }
        else {
            dirList.size
        }
    }


    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {
        if (dirList.isEmpty()) {
            holder.bind(File(""))
        } else {
            holder.bind(dirList[position])
        }
    }

    fun setList(list: List<File>) {
        this.dirList = list
        notifyDataSetChanged()
    }

    inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtDirName: TextView = itemView.findViewById(R.id.txtDirName)

        fun bind(dir: File) {
            txtDirName.text = when (dir == File("")) {
                true -> "All included paths"
                false -> dir.absolutePath
            }
        }
    }
}