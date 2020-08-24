package com.flamyoad.tsukiviewer.ui.editor

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.model.DoujinDetailsWithTags
import com.flamyoad.tsukiviewer.model.EditorHistoryItem
import com.flamyoad.tsukiviewer.model.Tag
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

        viewmodel.detailWithTags.observe(this, Observer { detail ->
            drawTags(detail)
        })
    }

    // If item is null, means its metadata does not exist in database
    private fun drawTags(item: DoujinDetailsWithTags?) {
        val parodies = item?.tags?.filter { x -> x.type == "parody" } ?: emptyList()
        val chars = item?.tags?.filter { x -> x.type == "character" } ?: emptyList()
        val tags = item?.tags?.filter { x -> x.type == "tag" } ?: emptyList()
        val artists = item?.tags?.filter { x -> x.type == "artist" } ?: emptyList()
        val groups = item?.tags?.filter { x -> x.type == "group" } ?: emptyList()
        val langs = item?.tags?.filter { x -> x.type == "language" } ?: emptyList()
        val categories = item?.tags?.filter { x -> x.type == "category" } ?: emptyList()

        val listofTagGroups = listOf(
            parodies, chars, tags, artists, groups, langs, categories
        )

        for (i in listofTagGroups.indices) {
            val chipGroup = when (i) {
                0 -> listParodies
                1 -> listCharacters
                2 -> listTags
                3 -> listArtists
                4 -> listGroups
                5 -> listLanguages
                6 -> listCategories
                else -> null
            }

            for (tag in listofTagGroups[i]) {
                insertTag(tag, chipGroup, BACKMOST_POSITION)
            }

            val chipNewTag = insertAddTagButton(chipGroup)

            val tagCategory = when (i) {
                0 -> "parody"
                1 -> "character"
                2 -> "tag"
                3 -> "artist"
                4 ->  "group"
                5 -> "language"
                6 -> "category"
                else -> ""
            }

            chipNewTag.setOnClickListener {
                showNewTagDialog(tagCategory)
            }
        }
    }

    private fun insertTag(tag: Tag, chipGroup: ChipGroup?, position: Int): Chip {
        val chip = layoutInflater.inflate(R.layout.tag_list_chip, chipGroup, false) as Chip
        chip.text = tag.name

        if (position == BACKMOST_POSITION) {
            chipGroup?.addView(chip)
        } else {
            Log.d("chipgroup", "Tag = ${tag.type}, Position = ${position}")
            chipGroup?.addView(chip, position)
        }

        chip.setOnCloseIconClickListener {
            val indexOfChip = chipGroup?.indexOfChild(chip) ?: 0

            chipGroup?.removeView(it)

            // Pushes tag item removed by user to undo stack
            val removedItem = EditorHistoryItem(tag, indexOfChip)
            viewmodel.pushUndo(removedItem)
        }
        return chip
    }

    private fun insertAddTagButton(chipGroup: ChipGroup?): Chip {
        val chip = layoutInflater.inflate(R.layout.tag_list_add, chipGroup, false) as Chip
        chip.text = "+"

        chipGroup?.addView(chip)

        return chip
    }

    private fun showNewTagDialog(category: String) {
        val bottomSheetDialog = TagBottomSheetDialog(category)
        bottomSheetDialog.show(supportFragmentManager, "tag_dialog")
    }

    private fun popUndoStack() {
        val oldItem = viewmodel.popUndo()

        if (oldItem != null) {
            val chipGroup = when (oldItem.tag.type) {
                "parody" -> listParodies
                "character" -> listCharacters
                "tag" -> listTags
                "artist" -> listArtists
                "group" -> listGroups
                "language" -> listLanguages
                "category" -> listCategories
                else -> null
            }

            insertTag(oldItem.tag, chipGroup, oldItem.index)
        }
    }

    override fun onTagCreated(name: String, type: String) {

    }


}
