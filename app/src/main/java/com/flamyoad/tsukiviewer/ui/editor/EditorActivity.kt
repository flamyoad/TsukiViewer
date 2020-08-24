package com.flamyoad.tsukiviewer.ui.editor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.model.EditorHistoryItem
import com.flamyoad.tsukiviewer.model.Tag
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.activity_editor.*

class EditorActivity : AppCompatActivity(), CreateTagListener {

    private val BACKMOST_POSITION = -1

    private lateinit var viewmodel: EditorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        viewmodel = ViewModelProvider(this).get(EditorViewModel::class.java)

        initToolbar()
        initTagGroups()

        viewmodel.hasCompletedSaving.observe(this, Observer { hasCompleted ->
            if (hasCompleted) {
                Toast.makeText(this, "Data is saved", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                contentLayout.isEnabled = false
                parentLayout.alpha = 0.5f
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tag_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.action_undo_edits -> {
                popUndoStack()
            }

            R.id.action_save_edits -> {
                viewmodel.save()
            }
        }
        return true
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val title = intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_NAME)
        txtDoujinTitle.text = title
    }

    private fun initTagGroups() {
        val dirPath = intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH)

        viewmodel.retrieveDoujinTags(dirPath)

        viewmodel.parody.observe(this, Observer {
            listParodies.setTagList("parody", it)
        })

        viewmodel.character.observe(this, Observer {
            listCharacters.setTagList("character", it)
        })

        viewmodel.tags.observe(this, Observer {
            listTags.setTagList("tag", it)
        })

        viewmodel.artist.observe(this, Observer {
            listArtists.setTagList("artist", it)
        })

        viewmodel.group.observe(this, Observer {
            listGroups.setTagList("group", it)
        })

        viewmodel.language.observe(this, Observer {
            listLanguages.setTagList("language", it)
        })

        viewmodel.category.observe(this, Observer {
            listCategories.setTagList("category", it)
        })
    }

    private fun ChipGroup.setTagList(category: String, tags: List<Tag>) {
        this.removeAllViews()

        for (tag in tags) {
            this.insertTag(tag, BACKMOST_POSITION)
        }

        val btnNewTag = this.insertAddTagButton()

        btnNewTag.setOnClickListener {
            showNewTagDialog(category)
        }
    }

    private fun ChipGroup.insertTag(tag: Tag, position: Int) {
        // Check for duplicates. If found, then return
        val chips = this.children as Sequence<Chip>

        for (chip in chips) {
            if (tag.name == chip.text) {
                return
            }
        }

        val chip = layoutInflater.inflate(R.layout.tag_list_chip, this, false) as Chip
        chip.text = tag.name

        if (position == BACKMOST_POSITION) {
            this.addView(chip)
        } else {
            this.addView(chip, position)
        }

        chip.setOnCloseIconClickListener {
            viewmodel.removeTag(tag.name, tag.type)
        }
    }

    private fun ChipGroup.insertAddTagButton(): Chip {
        val chip = layoutInflater.inflate(R.layout.tag_list_add, this, false) as Chip
        chip.text = "+"

        this.addView(chip)

        return chip
    }

    private fun popUndoStack() {
        viewmodel.popUndo()
    }

    override fun onTagCreated(name: String, category: String) {
        viewmodel.addTag(name, category)
        dismissNewTagDialog()
    }

    private fun showNewTagDialog(category: String) {
        val bottomSheetDialog = TagBottomSheetDialog()
        bottomSheetDialog.show(supportFragmentManager, "tag_dialog")

        viewmodel.retrieveTagsByCategory(category)
    }

    private fun dismissNewTagDialog() {
        val dialog =
            supportFragmentManager.findFragmentByTag("tag_dialog") as BottomSheetDialogFragment
        dialog.dismiss()
    }

}

