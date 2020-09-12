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

class DialogChangeName: DialogFragment() {

    companion object {
        const val COLLECTION_NAME = "collection_name"

        fun newInstance(name: String): DialogChangeName {
            val bundle = Bundle()
            bundle.putString(COLLECTION_NAME, name)

            val dialog = DialogChangeName()
            dialog.arguments = bundle

            return dialog
        }
    }

    private val viewmodel: CollectionDoujinViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val oldName = requireArguments().getString(COLLECTION_NAME)

        val builder = AlertDialog.Builder(requireContext())

        val view = layoutInflater.inflate(R.layout.dialog_add_collection, null, false)

        val fieldName: TextInputEditText = view.findViewById(R.id.fieldName)
        val fieldLayout: TextInputLayout = view.findViewById(R.id.fieldNameLayout)

        fieldName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewmodel.newCollectionName.value = text.toString()
            }
        })

        builder.setTitle(oldName)
        builder.setView(view)
        builder.setNegativeButton("Return", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        })

        builder.setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
            if (oldName.isNullOrBlank()) {
                return@OnClickListener
            }

            val newName = fieldName.text.toString()
            viewmodel.changeCollectionName(oldName, newName)
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