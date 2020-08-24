package com.flamyoad.tsukiviewer.ui.editor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
            setTagList("parody", it, listParodies)
        })

        viewmodel.character.observe(this, Observer {
            setTagList("character", it, listCharacters)
        })

        viewmodel.tags.observe(this, Observer {
            setTagList("tag", it, listTags)
        })

        viewmodel.artist.observe(this, Observer {
            setTagList("artist", it, listArtists)
        })

        viewmodel.group.observe(this, Observer {
            setTagList("group", it, listGroups)
        })

        viewmodel.language.observe(this, Observer {
            setTagList("language", it, listLanguages)
        })

        viewmodel.category.observe(this, Observer {
            setTagList("category", it, listCategories)
        })
    }

    private fun setTagList(category: String, tags: List<Tag>, chipGroup: ChipGroup?) {
        chipGroup?.removeAllViews()

        for (tag in tags) {
            insertTag(tag, chipGroup, BACKMOST_POSITION)
        }

        val btnNewTag = insertAddTagButton(chipGroup)

        btnNewTag.setOnClickListener {
            showNewTagDialog(category)
        }
    }

    private fun insertTag(tag: Tag, chipGroup: ChipGroup?, position: Int) {
        // Check for duplicates. If found, then return
        val chips = chipGroup?.children as Sequence<Chip>

        for (chip in chips) {
            if (tag.name == chip.text) {
                return
            }
        }

        val chip = layoutInflater.inflate(R.layout.tag_list_chip, chipGroup, false) as Chip
        chip.text = tag.name

        if (position == BACKMOST_POSITION) {
            chipGroup.addView(chip)
        } else {
            chipGroup.addView(chip, position)
        }

        chip.setOnCloseIconClickListener {
            val indexOfChip = chipGroup.indexOfChild(chip)

            viewmodel.removeTag(tag.name, tag.type)

            // Pushes tag item removed by user to undo stack
            val removedItem = EditorHistoryItem(tag, indexOfChip)
            viewmodel.pushUndo(removedItem)
        }
    }

    private fun insertAddTagButton(chipGroup: ChipGroup?): Chip {
        val chip = layoutInflater.inflate(R.layout.tag_list_add, chipGroup, false) as Chip
        chip.text = "+"

        chipGroup?.addView(chip)

        return chip
    }

    private fun popUndoStack() {
        val oldItem = viewmodel.popUndo()

        if (oldItem != null) {
            val categoryName = oldItem.tag.type
            val chipGroup = findChipGroupByCategory(categoryName)
            insertTag(oldItem.tag, chipGroup, oldItem.index)
        }
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

    private fun findChipGroupByCategory(category: String): ChipGroup? {
        return when (category) {
            "parody" -> listParodies
            "character" -> listCharacters
            "tag" -> listTags
            "artist" -> listArtists
            "group" -> listGroups
            "language" -> listLanguages
            "category" -> listCategories
            else -> null
        }
    }
}
