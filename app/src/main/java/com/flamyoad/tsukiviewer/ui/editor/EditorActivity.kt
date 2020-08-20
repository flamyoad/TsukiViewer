package com.flamyoad.tsukiviewer.ui.editor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.model.DoujinDetailsWithTags
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.activity_editor.*

class EditorActivity : AppCompatActivity() {

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

        viewmodel.retrieveTags(dirPath)

        viewmodel.detailWithTags.observe(this, Observer { detail ->
            drawTags(detail)
        })
    }

    private fun drawTags(item: DoujinDetailsWithTags) {
        val parodies = item.tags.filter { x -> x.type == "parody" }
        val chars = item.tags.filter { x -> x.type == "character" }
        val tags = item.tags.filter { x -> x.type == "tag" }
        val artists = item.tags.filter { x -> x.type == "artist" }
        val groups = item.tags.filter { x -> x.type == "group" }
        val langs = item.tags.filter { x -> x.type == "language" }
        val categories = item.tags.filter { x -> x.type == "category" }

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
                addChip(tag.name, R.layout.tag_list_chip, chipGroup)
            }

            val chipNewTag = addChip("+", R.layout.tag_list_add,  chipGroup)

            chipNewTag.setOnClickListener {

            }
        }
    }

    private fun addChip(text: String, layoutId: Int, chipGroup: ChipGroup?): Chip {
        val chip = layoutInflater.inflate(layoutId, chipGroup, false) as Chip
        chip.text = text

        chip.setOnCloseIconClickListener {
            chipGroup?.removeView(it)
        }

        chipGroup?.addView(chip)

        return chip
    }
}
