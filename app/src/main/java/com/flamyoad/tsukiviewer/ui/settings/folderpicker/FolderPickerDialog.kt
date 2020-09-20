package com.flamyoad.tsukiviewer.ui.settings.folderpicker

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Point
import android.os.Bundle
import android.os.Environment
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.FolderPickerAdapter
import com.flamyoad.tsukiviewer.adapter.ParentDirectoriesAdapter
import com.flamyoad.tsukiviewer.ui.settings.includedfolders.AddFolderListener
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class FolderPickerDialog : DialogFragment(),
    FolderPickerListener,
    ParentDirectoryListener {

    private val CURRENT_PATH_STRING = "current_path_string"

    private lateinit var listFolders: RecyclerView

    private lateinit var listTopDirs: RecyclerView

    private lateinit var foldersAdapter: FolderPickerAdapter

    private lateinit var topDirsAdapter: ParentDirectoriesAdapter

    private lateinit var currentDir: File

    private lateinit var addFolderListener: AddFolderListener

    private var job: Job? = null

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

        listFolders = view.findViewById(R.id.listContainedFolders)

        listTopDirs = view.findViewById(R.id.listParentDirectories)

        return dialogBuilder.create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_PATH_STRING, currentDir.toString())
    }

    override fun onResume() {
        super.onResume()
        val window: Window? = dialog!!.window
        window?.setGravity(Gravity.CENTER)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeDialog()
    }

    private fun initializeDialog() {
        initializeRecyclerViews()
        setRecyclerviewSize()

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
        foldersAdapter = FolderPickerAdapter(this)

        listFolders.adapter = foldersAdapter

        listFolders.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listFolders.setHasFixedSize(true)

        // Initialization code for recyclerview for list of parent directories
        topDirsAdapter = ParentDirectoriesAdapter(this)

        listTopDirs.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val linearSnapHelper = LinearSnapHelper()

        linearSnapHelper.attachToRecyclerView(listTopDirs)

        listTopDirs.adapter = topDirsAdapter
    }

    private fun setRecyclerviewSize() {
        // Set height and width of recyclerview to fixed size
        val window: Window? = dialog!!.window
        val size = Point()

        val display: Display? = window?.getWindowManager()?.getDefaultDisplay()
        display?.getSize(size)

        listFolders.apply {
            layoutParams.height = (size.y * 0.85).toInt()
            requestLayout()
        }
    }

    private fun fetchFolders(dir: File) {
        foldersAdapter.clearList()
        val dirList = mutableListOf<File>()

        job = lifecycleScope.launch(Dispatchers.IO) {
            val children = dir.listFiles()

            // Assume the parent directories like below

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
            // maybe try natural sort
            dirList.sortBy {
                it.name.toLowerCase(Locale.ROOT)
            }

            // Adds the "back-to-previous-folder" button in the beginning of list. Skip adding it if it's a root directory
            val upFolder = dir.parentFile
            if (upFolder.path != "/") {
                dirList.add(0, upFolder)
            }

            // Update UI in main thread
            withContext(Dispatchers.Main) {
                foldersAdapter.setCurrentDirectory(dir)
                foldersAdapter.setList(dirList)
            }
        }

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

        topDirsAdapter.setList(parentsOfCurrentPath)
    }

    override fun onFolderPick(dir: File) {
        currentDir = dir
        fetchFolders(dir)
        fetchParentDirectories(dir)
    }

    override fun onParentDirectoryClick(dir: File) {
        // Stop loading jobs from previous directory
        job?.cancel()

        currentDir = dir
        fetchFolders(dir)
        fetchParentDirectories(dir)
    }
}

