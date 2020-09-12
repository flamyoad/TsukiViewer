package com.flamyoad.tsukiviewer.ui.home.collection

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.flamyoad.tsukiviewer.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class DialogNewCollection: DialogFragment() {
    private val viewmodel: CollectionDoujinViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())

        val view = layoutInflater.inflate(R.layout.dialog_add_collection,null , false)

        val fieldName: TextInputEditText = view.findViewById(R.id.fieldName)
        val fieldLayout: TextInputLayout = view.findViewById(R.id.fieldNameLayout)

        fieldName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewmodel.newCollectionName.value = text.toString()
            }
        })

        builder.setTitle("Create new collection")
        builder.setView(view)
        builder.setNegativeButton("Return", DialogInterface.OnClickListener { dialogInterface, i ->
            viewmodel.newCollectionName.value = ""
        })

        builder.setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
            val name = fieldName.text.toString()
            viewmodel.createCollection(name)
            viewmodel.newCollectionName.value = ""
        })

        val dialog = builder.create()

        viewmodel.collectionNameExists.observe(this, Observer { alreadyExists ->
            when (alreadyExists) {
                true ->  {
                    fieldLayout.error = "The name is already used!"
                    dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = false
                }
                false -> {
                    fieldLayout.error = null
                    dialog.getButton(Dialog.BUTTON_POSITIVE).isEnabled = true
                }
            }
        })

        return dialog
    }
}