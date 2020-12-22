package com.flamyoad.tsukiviewer.ui.home.collections

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.model.Tag
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_create_collection.*

class CreateCollectionActivity : AppCompatActivity() {

    private val viewModel: CreateCollectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_collection)

        initToolbar()
        initUi()

        viewModel.titles().observe(this, Observer { values ->
            listTitles.removeAllViews()
            for (value in values) {
                insertTitle(value)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_collection, menu)
        return true
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        setTitle("Create new collection")
    }

    private fun initUi() {
        // Inflates default add button in the Included Tags
        val includedTagNewChip = layoutInflater.inflate(R.layout.tag_list_add, parentLayout, false) as Chip
        includedTagNewChip.text = "+"
        listIncludedTags.addView(includedTagNewChip)

        // Inflates default add button in the Excluded Tags
        val excludedTagNewChip = layoutInflater.inflate(R.layout.tag_list_add, parentLayout, false) as Chip
        excludedTagNewChip.text = "+"
        listExcludedTags.addView(excludedTagNewChip)

        fieldTitle.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.addTitle(textView.text.toString())
                textView.text = ""
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        // Disables the enter button of title if input is blank
        fieldTitle.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnAddTitle.isEnabled = count > 0
            }
        })

        btnAddTitle.setOnClickListener {
            viewModel.addTitle(fieldTitle.text.toString())
            fieldTitle.setText("")
        }
    }

    private fun insertTitle(title: String) {
        if (title.isBlank()) return

        val chip = layoutInflater.inflate(R.layout.tag_list_chip, parentLayout, false) as Chip
        chip.text = title

        chip.setOnCloseIconClickListener {
            viewModel.removeTitle(title)
        }

        listTitles.addView(chip)
    }

    private fun insertIncludedTag(tag: Tag) {

    }
}
