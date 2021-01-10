package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import java.io.File

class DirectoryPickerAdapter(private val onDirPick: (File) -> Unit)
    : RecyclerView.Adapter<DirectoryPickerAdapter.DirectoryViewHolder>() {

    private var dirList: List<File> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.dialog_collection_directory_picker_item, parent, false)

        val holder = DirectoryViewHolder(layout)


        holder.itemView.setOnClickListener {
            val dir = dirList[holder.bindingAdapterPosition]
            onDirPick(dir)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return dirList.size
    }

    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {
        holder.bind(dirList[position])
    }

    fun setList(dirList: List<File>) {
        this.dirList = dirList
        notifyDataSetChanged()
    }

    inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtDirectory: TextView = itemView.findViewById(R.id.txtDirectory)

        fun bind(dir: File) {
            txtDirectory.text = dir.absolutePath
        }
    }
}