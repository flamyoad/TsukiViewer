package com.flamyoad.tsukiviewer.ui.settings.includedfolders

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.ui.settings.folderpicker.FolderPickerDialog
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.IncludedFolderAdapter
import com.flamyoad.tsukiviewer.databinding.ActivityIncludedFolderBinding
import com.flamyoad.tsukiviewer.di.ViewModelFactory
import com.flamyoad.tsukiviewer.model.IncludedPath
import com.gun0912.tedpermission.coroutine.TedPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class IncludedFolderActivity : AppCompatActivity(),
    AddFolderListener,
    RemoveFolderListener {

    private lateinit var binding: ActivityIncludedFolderBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    
    private val viewModel: IncludedFolderViewModel by viewModels { viewModelFactory }

    private lateinit var adapter: IncludedFolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityIncludedFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        initRecyclerview()

        checkForPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_included_folder, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_folder -> {
                openFolderPicker()
            }
        }
        return false
    }

    private fun initRecyclerview() {
        adapter = IncludedFolderAdapter(this)
        binding.listDirectoryChosen.adapter = adapter

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.listDirectoryChosen.layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(
            this,
            linearLayoutManager.orientation
        )
        binding.listDirectoryChosen.addItemDecoration(dividerItemDecoration)

        viewModel.pathList.observe(this, Observer {
            adapter.setList(it)
        })
    }

    private fun checkForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ requires MANAGE_EXTERNAL_STORAGE
            if (!Environment.isExternalStorageManager()) {
                AlertDialog.Builder(this)
                    .setTitle("Storage Permission Required")
                    .setMessage("This app needs access to all files to browse your image folders. Please grant 'All files access' permission.")
                    .setPositiveButton("Grant") { _, _ ->
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                            data = Uri.parse("package:$packageName")
                        }
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                        Toast.makeText(this, "Permission required to browse folders", Toast.LENGTH_SHORT).show()
                    }
                    .show()
            }
        } else {
            // Android 10 and below - use runtime permissions
            CoroutineScope(Dispatchers.Main).launch {
                val permissionResult = TedPermission.create()
                    .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .check()
                
                if (!permissionResult.isGranted) {
                    Toast.makeText(this@IncludedFolderActivity, "Permission Denied\n" + permissionResult.deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openFolderPicker() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val dialogFragment =
            FolderPickerDialog()
        dialogFragment.show(fragmentTransaction, "dialog")
        dialogFragment.setAddFolderListener(this)
    }

    override fun addFolder(dir: File) {
        viewModel.insert(dir)
    }

    override fun deleteFolder(includedPath: IncludedPath) {
        viewModel.delete(includedPath)
    }

}

interface AddFolderListener {
    fun addFolder(dir: File)
}

interface RemoveFolderListener {
    fun deleteFolder(includedPath: IncludedPath)
}
