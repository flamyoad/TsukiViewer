package com.flamyoad.tsukiviewer.ui.home.local

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.SourceSelectorAdapter
import com.flamyoad.tsukiviewer.model.Source
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*
import kotlin.collections.HashMap

class DialogSelectSource : DialogFragment() {
//    private val viewModel: LocalDoujinViewModel by activityViewModels()

    private val checkBoxStates = HashMap<Source, Boolean>()

    private val sourceAdapter = SourceSelectorAdapter(checkBoxStates, this::onCheckBoxClick)

    private var listener: SelectSourceListener? = null

    private lateinit var listSources: RecyclerView

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

        val dialog = builder.create()

        val txtTarget = view.findViewById<MaterialTextView>(R.id.txtTarget)
        txtTarget.text = getString(R.string.source_selector_target, "All")

        listSources = view.findViewById(R.id.listSources)

        val btnStart = view.findViewById<Button>(R.id.btnStart)
        btnStart.setOnClickListener {
            startFetching()
            dialog.dismiss()
        }

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
                val isChecked = key.readableName in selectedSources
                entry.setValue(isChecked)
            }
            sourceAdapter.notifyDataSetChanged()
        }
    }

    private fun onCheckBoxClick(source: Source, newValue: Boolean) {
        checkBoxStates[source] = newValue
    }

    private fun startFetching() {
        val selectedSources: List<Source> = checkBoxStates
            .filter { pair -> pair.value == true }
            .map { pair -> Source.valueOf(pair.key.name) }

        val sourceFlags = EnumSet.copyOf(selectedSources)

        listener!!.onFetchMetadata(sourceFlags)
    }

    companion object {
        const val name = "DIALOG_SELECT_SOURCE"
        const val CHECKBOX_STATES = "CHECKBOX_STATES"
        fun newInstance() = DialogSelectSource()
    }
}