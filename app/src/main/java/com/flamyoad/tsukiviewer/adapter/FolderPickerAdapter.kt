package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.ui.settings.folderpicker.FolderPickerListener
import com.flamyoad.tsukiviewer.R
import java.io.File

class FolderPickerAdapter(private val listener: FolderPickerListener)
    : RecyclerView.Adapter<FolderPickerAdapter.FileHolder>() {

    private var list: List<File> = emptyList()

    private var currentDir: File? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.folder_list_item, parent, false)
        val holder = FileHolder(view)

        view.setOnClickListener {
            val dir = list[holder.adapterPosition]
            listener.onFolderPick(dir)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        holder.bind(list[position])
    }

    fun setCurrentDirectory(dir: File) {
        currentDir = dir
    }

    fun setList(list: List<File>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class FileHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtFolderName = itemView.findViewById<TextView>(R.id.txtFolderName)

        fun bind(file: File) {
            if (currentDir!!.parentFile.name == file.name) {
                txtFolderName.text = ".."
            } else {
                txtFolderName.text = file.name
            }
        }
    }
}