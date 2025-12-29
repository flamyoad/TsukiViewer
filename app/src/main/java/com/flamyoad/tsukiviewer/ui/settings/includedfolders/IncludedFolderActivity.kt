package com.flamyoad.tsukiviewer.ui.settings.includedfolders

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.tsukiviewer.ui.settings.folderpicker.FolderPickerDialog
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.IncludedFolderAdapter
import com.flamyoad.tsukiviewer.databinding.ActivityIncludedFolderBinding
import com.flamyoad.tsukiviewer.model.IncludedPath
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.File


class IncludedFolderActivity : AppCompatActivity(),
    AddFolderListener,
    RemoveFolderListener {

    private lateinit var binding: ActivityIncludedFolderBinding

    private val viewModel: IncludedFolderViewModel by viewModels()

    private lateinit var adapter: IncludedFolderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
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
        val listener = object: PermissionListener {
            override fun onPermissionGranted() {

            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@IncludedFolderActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        TedPermission.with(this)
            .setPermissionListener(listener)
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            .check()
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
