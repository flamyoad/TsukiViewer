package com.flamyoad.tsukiviewer.ui.home.local

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.SourceSelectorAdapter
import com.flamyoad.tsukiviewer.core.model.Source
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.util.*
import kotlin.collections.HashMap

class DialogSelectSource : DialogFragment() {
    private val checkBoxStates = HashMap<Source, Boolean>()

    private val sourceAdapter = SourceSelectorAdapter(checkBoxStates, this::onCheckBoxClick)

    private var listener: SelectSourceListener? = null

    private lateinit var listSources: RecyclerView
    private lateinit var btnStart: MaterialButton
    private lateinit var txtVpnLink: MaterialTextView

    init {
        // All checkboxes should be ticked by default
        for (source in Source.values()) {
            checkBoxStates.put(source, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (targetFragment is SelectSourceListener) {
            listener = targetFragment as SelectSourceListener
        } else {
            throw RuntimeException("$context must implement SelectSourceListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_fetch_sources, null, false)
        builder.setView(view)

        listSources = view.findViewById(R.id.listSources)
        btnStart = view.findViewById(R.id.btnStart)
        txtVpnLink = view.findViewById(R.id.txtVpnLink)

        val txtTarget = view.findViewById<MaterialTextView>(R.id.txtTarget)
        arguments?.let {
            val targetDir = it.getString(TARGET_DIR)
            txtTarget.text = targetDir
        }

        val btnStart = view.findViewById<Button>(R.id.btnStart)
        btnStart.setOnClickListener {
            startFetching()
        }

        val dialog = builder.create()

        return dialog
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val selectedSources = checkBoxStates
            .filter { pair -> pair.value == true }
            .map { pair -> pair.key.name }
            .toTypedArray()

        outState.putStringArray(CHECKBOX_STATES, selectedSources)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listSources.apply {
            adapter = sourceAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        if (savedInstanceState != null) {
            val selectedSources = savedInstanceState.getStringArray(CHECKBOX_STATES) ?: return
            for (entry in checkBoxStates) {
                val key = entry.key
                val isChecked = key.name in selectedSources
                entry.setValue(isChecked)
            }
            sourceAdapter.notifyDataSetChanged()
        }

        // Set up hyperlink
        txtVpnLink.apply {
            setMovementMethod(LinkMovementMethod.getInstance())
            setLinkTextColor(Color.BLUE)
        }
    }

    private fun onCheckBoxClick(source: Source, newValue: Boolean) {
        checkBoxStates[source] = newValue

        // If none of the checkboxes are ticked, disable the button
        val chosenAtLeastOne = checkBoxStates.any { pair -> pair.value == true }
        btnStart.isEnabled = chosenAtLeastOne
    }

    private fun startFetching() {
        val selectedSources: List<Source> = checkBoxStates
            .filter { pair -> pair.value == true }
            .map { pair -> Source.valueOf(pair.key.name) }

        val sourceFlags = EnumSet.copyOf(selectedSources)
        listener!!.onFetchMetadata(sourceFlags)

        dialog?.dismiss()
    }

    companion object {
        const val name = "DIALOG_SELECT_SOURCE"

        const val CHECKBOX_STATES = "CHECKBOX_STATES"
        const val TARGET_DIR = "TARGET_DIR"

        fun newInstance(targetDir: String = "All") =
            DialogSelectSource().apply {
                arguments = Bundle().apply {
                    putString(TARGET_DIR, targetDir)
                }
            }

    }
}
