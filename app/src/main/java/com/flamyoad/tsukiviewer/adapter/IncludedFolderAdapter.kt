package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.ui.settings.includedfolders.RemoveFolderListener
import com.flamyoad.tsukiviewer.model.IncludedFolder

class IncludedFolderAdapter(private val listener: RemoveFolderListener) :
    RecyclerView.Adapter<IncludedFolderAdapter.FolderViewHolder>() {

    private var folderList = emptyList<IncludedFolder>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
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

    override fun getItemCount(): Int {
        return folderList.size
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(folderList[holder.adapterPosition])
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