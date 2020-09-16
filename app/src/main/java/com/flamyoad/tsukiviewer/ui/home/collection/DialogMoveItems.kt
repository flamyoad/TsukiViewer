package com.flamyoad.tsukiviewer.ui.home.collection

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import com.flamyoad.tsukiviewer.model.DoujinCollection

class DialogMoveItems(private val collectionList: LiveData<List<DoujinCollection>>)
    : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
}