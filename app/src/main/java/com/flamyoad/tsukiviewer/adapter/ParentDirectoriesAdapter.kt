package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.ui.settings.folderpicker.ParentDirectoryListener
import com.flamyoad.tsukiviewer.R
import java.io.File

class ParentDirectoriesAdapter(private val listener: ParentDirectoryListener) :
    RecyclerView.Adapter<ParentDirectoriesAdapter.ParentDirectoryHolder>() {

    private var dirList = emptyList<File>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentDirectoryHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.folderpicker_parent_directories_item, parent, false)

        val holder = ParentDirectoryHolder(view)

        view.setOnClickListener {
            val dir = dirList[holder.adapterPosition]
            listener.onParentDirectoryClick(dir)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return dirList.size
    }

    override fun onBindViewHolder(holder: ParentDirectoryHolder, position: Int) {
        val dir = dirList[holder.adapterPosition]
        holder.bind(dir)
    }

    fun setList(dirList: List<File>) {
        this.dirList = dirList
        notifyDataSetChanged()
    }

    inner class ParentDirectoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtDirName = itemView.findViewById<TextView>(R.id.txtDirName)

        fun bind(file: File) {
            txtDirName.paint.isUnderlineText = true
            txtDirName.text = file.name
        }
    }

}