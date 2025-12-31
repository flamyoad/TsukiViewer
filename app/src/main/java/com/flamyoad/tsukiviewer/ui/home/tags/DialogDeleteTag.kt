package com.flamyoad.tsukiviewer.ui.home.tags

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.flamyoad.tsukiviewer.core.model.Tag

class DialogDeleteTag: DialogFragment() {

    private val viewModel: DoujinTagsViewModel by activityViewModels()

    private var tagId: Long = -1
    private var tagName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tagId = it.getLong(TAG_ID, -1)
            tagName = it.getString(TAG_NAME) ?: ""
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        builder.apply {
            setTitle("Remove this tag? This action cannot be undone")
            setMessage(tagName)
            setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                if (tagId != -1L)
                viewModel.removeTag(tagId)
                dialog?.dismiss()
            })
            setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->

            })
        }

        return builder.create()
    }

    companion object {
        const val name = "DIALOG_DELETE_TAG"
        const val TAG_ID = "TAG_ID"
        const val TAG_NAME = "TAG_NAME"

        fun newInstance(tag: Tag) = DialogDeleteTag().apply {
            arguments = Bundle().apply {
                putLong(TAG_ID, tag.tagId ?: -1)
                putString(TAG_NAME, tag.name)
            }
        }
    }
}