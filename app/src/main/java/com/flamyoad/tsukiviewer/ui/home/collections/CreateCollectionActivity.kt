package com.flamyoad.tsukiviewer.ui.home.collections

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.CollectionFilterDirectoryAdapter
import com.flamyoad.tsukiviewer.databinding.ActivityCreateCollectionBinding
import com.flamyoad.tsukiviewer.di.ViewModelFactory
import com.flamyoad.tsukiviewer.model.Collection
import com.flamyoad.tsukiviewer.model.Tag
import com.flamyoad.tsukiviewer.ui.search.TagSelectedListener
import com.flamyoad.tsukiviewer.utils.extensions.toast
import com.google.android.material.chip.Chip
import java.io.File
import javax.inject.Inject

private const val REQUEST_DIR_PICKER = 101

class CreateCollectionActivity : AppCompatActivity(), TagSelectedListener {

    private lateinit var binding: ActivityCreateCollectionBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    
    private val viewModel: CreateCollectionViewModel by viewModels { viewModelFactory }

    private lateinit var dirAdapter: CollectionFilterDirectoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val collectionId = intent.getLongExtra(COLLECTION_ID, -1L)
        viewModel.initCollectionData(collectionId)

        initToolbar(collectionId)
        initUi()

        viewModel.titles().observe(this, Observer { values ->
            binding.txtTitles.removeAllViews()
            for (value in values) {
                insertTitle(value)
            }
        })

        viewModel.currentCollection().observe(this, Observer {
            binding.fieldCollectionName.setText(it.name)

            if (it.minNumPages != Int.MIN_VALUE) {
                binding.fieldMinNumPages.setText(it.minNumPages.toString())
            }

            if (it.maxNumPages != Int.MAX_VALUE) {
                binding.fieldMaxNumPages.setText(it.maxNumPages.toString())
            }
        })

        viewModel.dirList().observe(this, Observer {
            dirAdapter.setList(it)
        })

        viewModel.includedTags().observe(this, Observer {
            refreshIncludedTags(it)
        })

        viewModel.excludedTags().observe(this, Observer {
            refreshExcludedTags(it)
        })

        viewModel.mustHaveAllIncludedTags().observe(this, Observer {
            binding.checkboxIncludedTags.isChecked = it
        })

        viewModel.mustHaveAllExcludedTags().observe(this, Observer {
            binding.checkboxExcludedTags.isChecked  = it
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_collection, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> submitCollection()
            android.R.id.home -> finish()
        }
        return true
    }

    private fun initToolbar(collectionId: Long) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        if (collectionId == -1L) {
            setTitle("Create new collection")
        } else {
            setTitle("Edit existing collection")
        }
    }

    private fun initUi() {
        dirAdapter = CollectionFilterDirectoryAdapter(this::openDirectoryPicker, viewModel::removeDir)
        binding.listDirs.adapter = dirAdapter
        binding.listDirs.layoutManager = LinearLayoutManager(this)

        // Inflates default add button in the Included Tags
        val includedTagNewChip = layoutInflater.inflate(R.layout.tag_list_add, binding.parentLayout, false) as Chip
        includedTagNewChip.text = "+"
        includedTagNewChip.setOnClickListener {
            openTagPicker(DialogTagPicker.Mode.Inclusive)
        }
        binding.listIncludedTags.addView(includedTagNewChip)

        // Inflates default add button in the Excluded Tags
        val excludedTagNewChip = layoutInflater.inflate(R.layout.tag_list_add, binding.parentLayout, false) as Chip
        excludedTagNewChip.text = "+"
        excludedTagNewChip.setOnClickListener {
            openTagPicker(DialogTagPicker.Mode.Exclusive)
        }
        binding.listExcludedTags.addView(excludedTagNewChip)

        binding.fieldTitle.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.addTitle(textView.text.toString())
                textView.text = ""
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        // Disables the enter button of title if input is blank
        binding.fieldTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(str: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btnAddTitle.isEnabled = !str.isNullOrBlank()
            }
        })

        binding.fieldMinNumPages.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(str: CharSequence?, start: Int, before: Int, count: Int) {
                val minNumPages = binding.fieldMinNumPages.text.toString().toIntOrNull() ?: Int.MIN_VALUE
                val maxNumPages = binding.fieldMaxNumPages.text.toString().toIntOrNull() ?: Int.MAX_VALUE

                if (minNumPages > maxNumPages) {
                    binding.layoutMinNumPages.error = "Invalid Input"
                } else {
                    binding.layoutMinNumPages.isErrorEnabled = false
                    binding.layoutMaxNumPages.isErrorEnabled = false
                }
            }
        })

        binding.fieldMaxNumPages.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(str: CharSequence?, start: Int, before: Int, count: Int) {
                val minNumPages = binding.fieldMinNumPages.text.toString().toIntOrNull() ?: Int.MIN_VALUE
                val maxNumPages = binding.fieldMaxNumPages.text.toString().toIntOrNull() ?: Int.MAX_VALUE

                if (minNumPages > maxNumPages) {
                    binding.layoutMaxNumPages.error = "Invalid Input"
                } else {
                    binding.layoutMinNumPages.isErrorEnabled = false
                    binding.layoutMaxNumPages.isErrorEnabled = false
                }
            }
        })

        binding.btnAddTitle.setOnClickListener {
            viewModel.addTitle(binding.fieldTitle.text.toString())
            binding.fieldTitle.setText("")
        }

        binding.checkboxIncludedTags.setOnCheckedChangeListener { compoundButton, bool ->
            viewModel.setMustHaveAllIncludedTags(bool)
        }

        binding.checkboxExcludedTags.setOnCheckedChangeListener { compoundButton, bool ->
            viewModel.setMustHaveAllExcludedTags(bool)
        }
    }

    private fun insertTitle(title: String) {
        if (title.isBlank()) return

        val chip = layoutInflater.inflate(R.layout.tag_list_chip, binding.parentLayout, false) as Chip
        chip.text = title

        chip.setOnCloseIconClickListener {
            viewModel.removeTitle(title)
        }

        binding.txtTitles.addView(chip)
    }

    private fun refreshIncludedTags(tags: List<Tag>) {
        binding.listIncludedTags.removeAllViews()

        // Inflates default add button in the Included Tags
        val includedTagNewChip =
            layoutInflater.inflate(R.layout.tag_list_add, binding.parentLayout, false) as Chip
        includedTagNewChip.text = "+"
        includedTagNewChip.setOnClickListener {
            openTagPicker(DialogTagPicker.Mode.Inclusive)
        }
        binding.listIncludedTags.addView(includedTagNewChip)

        for (tag in tags) {
            val chip = layoutInflater.inflate(R.layout.tag_list_chip, null, false) as Chip
            chip.text = tag.type + ": " + tag.name
            chip.setOnCloseIconClickListener {
                viewModel.removeIncludedTag(tag)
            }

            binding.listIncludedTags.addView(chip)
        }
    }

    private fun refreshExcludedTags(tags: List<Tag>) {
        binding.listExcludedTags.removeAllViews()

        // Inflates default add button in the Excluded Tags
        val excludedTagNewChip =
            layoutInflater.inflate(R.layout.tag_list_add, binding.parentLayout, false) as Chip
        excludedTagNewChip.text = "+"
        excludedTagNewChip.setOnClickListener {
            openTagPicker(DialogTagPicker.Mode.Exclusive)
        }
        binding.listExcludedTags.addView(excludedTagNewChip)

        for (tag in tags) {
            val chip = layoutInflater.inflate(R.layout.tag_list_chip, null, false) as Chip
            chip.text = tag.type + ": " + tag.name
            chip.setOnCloseIconClickListener {
                viewModel.removeExcludedTag(tag)
            }

            binding.listExcludedTags.addView(chip)
        }
    }

    private fun submitCollection() {
        val existingCollectionId = intent.getLongExtra(COLLECTION_ID, -1)

        val collectionName = binding.fieldCollectionName.text.toString()
        if (collectionName.isBlank()) {
            toast("You must at least give it a name!")
            return
        }

        val minNumPagesInput = binding.fieldMinNumPages.text.toString()
        val minNumPages = when (minNumPagesInput.isBlank()) {
            true -> Int.MIN_VALUE
            false -> minNumPagesInput.toInt()
        }

        val maxNumPagesInput = binding.fieldMaxNumPages.text.toString()
        val maxNumPages = when (maxNumPagesInput.isBlank()) {
            true -> Int.MAX_VALUE
            false -> maxNumPagesInput.toInt()
        }

        if (minNumPages > maxNumPages) {
            toast("Invalid range of page numbers")
            binding.scrollView.postDelayed({
                binding.scrollView.smoothScrollTo(0, binding.layoutMetadata.bottom)
            }, 100)
            return
        }

        val collection = Collection(
            id = when (existingCollectionId == -1L) {
                true -> null
                false -> existingCollectionId
            },
            name = collectionName,
            coverPhoto = File(""),
            minNumPages = minNumPages,
            maxNumPages = maxNumPages,
            mustHaveAllTitles = false, // Hardcoded to use OR logic for now
            mustHaveAllIncludedTags = binding.checkboxIncludedTags.isChecked,
            mustHaveAllExcludedTags = binding.checkboxExcludedTags.isChecked
        )

        viewModel.submitCollection(collection)

        finish()
    }

    private fun openDirectoryPicker() {
        val dialog = DialogDirectoryPicker.newInstance()
        dialog.show(supportFragmentManager, DialogDirectoryPicker.NAME)
    }

    private fun openTagPicker(mode: DialogTagPicker.Mode) {
        viewModel.tagPickerMode = mode
        val dialog = DialogTagPicker.newInstance()
        dialog.show(supportFragmentManager, DialogTagPicker.NAME)
    }

    override fun onTagSelected(tag: Tag) {
        when (viewModel.tagPickerMode) {
            DialogTagPicker.Mode.Inclusive -> {
                viewModel.addIncludedTag(tag)
            }
            DialogTagPicker.Mode.Exclusive -> {
                viewModel.addExcludedTag(tag)
            }
            DialogTagPicker.Mode.None -> {
                // Do nothing
            }
        }

        // Dismisses the dialog once an item is clicked
        val dialog =
            supportFragmentManager.findFragmentByTag(DialogTagPicker.NAME) as DialogFragment
        dialog.dismiss()

        viewModel.clearQuery()
    }

    companion object {
        const val COLLECTION_ID = "collection_id"
    }
}
