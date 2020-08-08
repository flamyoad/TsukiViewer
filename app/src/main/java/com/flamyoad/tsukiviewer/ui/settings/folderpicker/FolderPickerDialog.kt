package com.flamyoad.tsukiviewer.ui.settings.folderpicker

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.FolderPickerAdapter
import com.flamyoad.tsukiviewer.adapter.ParentDirectoriesAdapter
import com.flamyoad.tsukiviewer.ui.settings.includedfolders.AddFolderListener
import java.io.File

class FolderPickerDialog : DialogFragment(),
    FolderPickerListener,
    ParentDirectoryListener {

    val CURRENT_PATH_STRING = "current_path_string"

    private lateinit var listContainedFolders: RecyclerView

    private lateinit var listParentDirectories: RecyclerView

    private lateinit var folderListAdapter: FolderPickerAdapter

    private lateinit var parentDirectoryAdapter: ParentDirectoriesAdapter

    private lateinit var currentDir: File

    private lateinit var addFolderListener: AddFolderListener

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_PATH_STRING, currentDir.toString())
    }

    fun setAddFolderListener(listener: AddFolderListener) {
        addFolderListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (savedInstanceState != null) {
            val pathString = savedInstanceState.getString(CURRENT_PATH_STRING)
            currentDir = File(pathString)
        }

        val dialogBuilder = AlertDialog.Builder(requireActivity())
            .setTitle("Pick folder")
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                addFolderListener.addFolder(currentDir)
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                dialog?.dismiss()
            })

        // View is inflated here
        val view = LayoutInflater.from(context)
            .inflate(R.layout.folder_picker_dialog, null, false)
        onViewCreated(view, null)
        dialogBuilder.setView(view)

        listContainedFolders = view.findViewById(R.id.listContainedFolders)

        listParentDirectories = view.findViewById(R.id.listParentDirectories)

        return dialogBuilder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeDialog()
    }

    private fun initializeDialog() {
        initializeRecyclerViews()

        if (this::currentDir.isInitialized) {
            fetchFolders(currentDir)
            fetchParentDirectories(currentDir)

        } else {
            val internalStorage = Environment.getExternalStorageDirectory()

            currentDir = internalStorage

            fetchFolders(internalStorage)
            fetchParentDirectories(internalStorage)
        }
    }

    private fun initializeRecyclerViews() {
        // Initialization code for recyclerview for list of choosable folders
        folderListAdapter = FolderPickerAdapter(this)

        listContainedFolders.adapter = folderListAdapter

        listContainedFolders.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listContainedFolders.setHasFixedSize(true)

        // Initialization code for recyclerview for list of parent directories
        parentDirectoryAdapter = ParentDirectoriesAdapter(this)

        listParentDirectories.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val linearSnapHelper = LinearSnapHelper()

        linearSnapHelper.attachToRecyclerView(listParentDirectories)

        listParentDirectories.adapter = parentDirectoryAdapter
    }

    // TODO: make it run asynchronously
    private fun fetchFolders(dir: File) {
        val dirList = mutableListOf<File>()

        val children = dir.listFiles()

        // Imagine the parent directories like below

        // storage > emulated > 0

        // The program crashes if the user clicks on "emulated". But it doesn't crash when click on storage or 0

        // The same situation can be observed in QuickPic

        // Cause: java.lang.NullPointerException: Attempt to get length of null array

        // As alternative, we just check for null here
        if (children != null) {
            for (file in children) {
                if (file.isDirectory) {
                    dirList.add(file)
                }
            }
        }

        // Sorts the list alphabetically
        //TODO: it sorts like this > (A, B, C... a, b, c...)
        // fix it so it becomes like this (A, a, B, b, C, c...)
        dirList.sortBy {
            it.name
        }

        // Adds the back-to-previous-folder button in the beginning of list. Skip adding it if it's a root directory
        val upFolder = dir.parentFile
        if (upFolder.path != "/") {
            dirList.add(0, upFolder)
        }

        folderListAdapter.setCurrentDirectory(dir)

        folderListAdapter.setList(dirList)
    }

    private fun fetchParentDirectories(dir: File) {
        // (path: File) is a temporary variable to be traversed for parent directories.
        var path = dir

        val parentsOfCurrentPath = mutableListOf<File>()

        while (path.parentFile != null) {

            // Add the item to the beginning of the list, otherwise the ordering would be reversed
            parentsOfCurrentPath.add(0, path)

            path = path.parentFile
        }

        parentDirectoryAdapter.setList(parentsOfCurrentPath)
    }

    override fun onFolderPick(dir: File) {
        currentDir = dir
        fetchFolders(dir)
        fetchParentDirectories(dir)
    }

    override fun onParentDirectoryClick(dir: File) {
        currentDir = dir
        fetchFolders(dir)
        fetchParentDirectories(dir)
    }
}

