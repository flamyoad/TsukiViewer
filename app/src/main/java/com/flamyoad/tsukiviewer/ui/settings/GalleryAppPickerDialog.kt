package com.flamyoad.tsukiviewer.ui.settings

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Point
import android.os.Bundle
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.GalleryPickerAdapter
import com.flamyoad.tsukiviewer.ui.settings.preferences.GalleryPickListener

class GalleryAppPickerDialog(private val listener: GalleryPickListener)
    : DialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(listener: GalleryPickListener)
                = GalleryAppPickerDialog(listener)
    }

    private lateinit var viewmodel: SettingsViewModel

    private lateinit var listGalleries: RecyclerView

    override fun onResume() {
        super.onResume()
        val window: Window? = dialog!!.window
        val size = Point()

        val display: Display? = window?.getWindowManager()?.getDefaultDisplay()
        display?.getSize(size)

        // Set the width of the dialog proportional to ?? % of the screen width
        // Set the height of the dialog proportional to ?? % of the screen height
        window?.setLayout((size.x * 0.85).toInt(), (size.y * 0.85).toInt())
        window?.setGravity(Gravity.CENTER)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Pick an application")
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->

            })
            .setNegativeButton("Return", DialogInterface.OnClickListener { dialogInterface, i ->

            })

        val view = LayoutInflater.from(context)
            .inflate(R.layout.gallery_picker_dialog, null, false)

        listGalleries = view.findViewById(R.id.listGalleries)

        builder.setView(view)

        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewmodel = ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java)

        val adapter = GalleryPickerAdapter(listener)
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        listGalleries.adapter = adapter
        listGalleries.layoutManager = linearLayoutManager

        viewmodel.packageAppList().observe(requireActivity(), Observer {
            adapter.setList(it)
        })
    }
}