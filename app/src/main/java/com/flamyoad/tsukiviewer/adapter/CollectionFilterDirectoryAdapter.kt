package com.flamyoad.tsukiviewer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import java.io.File

private const val ADD_NEW = 0
private const val DIR_ITEM = 1

class CollectionFilterDirectoryAdapter
    (private val onAddDir: () -> Unit, private val onRemoveDir: (File) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dirList: List<File> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ADD_NEW -> {
                val view = layoutInflater.inflate(R.layout.collection_directory_filter_add_btn, parent, false)
                val holder = AddButtonViewHolder(view)

                val layout: ConstraintLayout = view.findViewById(R.id.layout)

                layout.setOnClickListener {
                    onAddDir()
                }

                return holder
            }

            DIR_ITEM -> {
                val view =
                    layoutInflater.inflate(R.layout.collection_directory_filter_item, parent, false)
                val holder = DirectoryViewHolder(view)

                val btnRemove: ImageButton = view.findViewById(R.id.btnRemove)
                btnRemove.setOnClickListener {
                    val dir = dirList[holder.adapterPosition]
                    onRemoveDir(dir)
                }

                return holder
            }

            else -> throw IllegalArgumentException("View type does not exist")
        }
    }

    override fun getItemCount(): Int {
        return dirList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        if (position == dirList.size) {
            return ADD_NEW
        } else {
            return DIR_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (dirList.isEmpty() || position == dirList.size) return

        val dir = dirList[position]
        when (holder.itemViewType) {
            DIR_ITEM -> (holder as DirectoryViewHolder).bind(dir)
        }
    }

    fun setList(dirList: List<File>) {
        this.dirList = dirList
        notifyDataSetChanged()
    }

    inner class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtName: TextView = itemView.findViewById(R.id.txtName)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)

        fun bind(file: File) {
            txtName.text = file.absolutePath
        }
    }

    inner class AddButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}