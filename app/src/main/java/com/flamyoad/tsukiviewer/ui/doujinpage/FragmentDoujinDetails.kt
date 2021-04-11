package com.flamyoad.tsukiviewer.ui.doujinpage

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.DoujinTagsAdapter
import com.flamyoad.tsukiviewer.adapter.LocalDoujinsAdapter
import com.flamyoad.tsukiviewer.model.DoujinDetails
import com.flamyoad.tsukiviewer.model.DoujinDetailsWithTags
import com.flamyoad.tsukiviewer.model.Source
import com.flamyoad.tsukiviewer.network.FetchMetadataService
import com.flamyoad.tsukiviewer.ui.home.local.DialogSelectSource
import com.flamyoad.tsukiviewer.ui.home.local.SelectSourceListener
import com.flamyoad.tsukiviewer.utils.ActivityHistory
import com.flamyoad.tsukiviewer.utils.ActivityStackUtils
import com.flamyoad.tsukiviewer.utils.TimeUtils
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.doujin_details_tags_group.*
import kotlinx.android.synthetic.main.fragment_doujin_details.*
import java.io.File
import java.util.*

private const val COLLECTION_DIALOG_TAG = "collection_dialog"

class FragmentDoujinDetails : Fragment(), SelectSourceListener {
    private val viewModel: DoujinViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_doujin_details, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_doujin_details_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sync -> {
                syncMetadata()
            }
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        initUi()
    }

    override fun onResume() {
        super.onResume()
        Log.d("debugs", "onResume() called")
        viewModel.coverImage().observe(viewLifecycleOwner, Observer { image ->
            Glide.with(this)
                .load(image)
                .sizeMultiplier(0.75f)
                .into(imgBackground)

            Glide.with(this)
                .load(image)
                .into(imgCover)
        })
    }

    override fun onStop() {
        super.onStop()
        Glide.with(this).clear(imgBackground)
        Glide.with(this).clear(imgCover)
    }

    private fun initUi() {
        viewModel.coverImage().observe(viewLifecycleOwner, Observer { image ->
            Glide.with(this)
                .load(image)
                .sizeMultiplier(0.75f)
                .into(imgBackground)

            Glide.with(this)
                .load(image)
                .into(imgCover)
        })

        viewModel.detailWithTags.observe(viewLifecycleOwner, Observer {
            val currentPath = requireActivity().intent
                .getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH)

            val dir = File(currentPath ?: "")

            txtDirectory.text = dir.absolutePath
            txtDateModified.text = TimeUtils.getReadableDate(dir.lastModified())

            if (it == null) {
                // Show directory name if metadata not yet obtained from API
                txtTitleEng.text = dir.name

                // Hides the tag group in case the user deletes the title & tags
                tagGroup.visibility = View.GONE
                tagsNotFoundIndicator.visibility = View.VISIBLE
            } else {
                // Shows the tag group if data is found in database
                tagGroup.visibility = View.VISIBLE
                tagsNotFoundIndicator.visibility = View.INVISIBLE
                initDoujinDetails(it)
            }
        })

        viewModel.imageList().observe(viewLifecycleOwner, Observer {
            txtImageCount.text = it.size.toString()
        })

        fab.setOnClickListener {
            viewModel.fetchBookmarkGroup()
            openCollectionDialog()
        }
    }

    private fun initDoujinDetails(item: DoujinDetailsWithTags) {
        initColoredEnglishTitle(item.doujinDetails)

        txtTitleJap.text = item.doujinDetails.fullTitleJapanese

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
            val group = listofTagGroups[i]

            val adapter = DoujinTagsAdapter(
                useLargerView = false,
                saveActivityInfo = this::saveActivityInfo
            )
            adapter.setList(group)

            val flexLayoutManager = FlexboxLayoutManager(context)
            flexLayoutManager.apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }

            val recyclerView = when (i) {
                0 -> listParodies
                1 -> listCharacters
                2 -> listTags
                3 -> listArtists
                4 -> listGroups
                5 -> listLanguages
                6 -> listCategories
                else -> null
            }

            recyclerView?.adapter = adapter
            recyclerView?.layoutManager = flexLayoutManager

            // Disables scrolling of the recyclerviews
            recyclerView?.suppressLayout(true)
        }
    }

    private fun initColoredEnglishTitle(doujinDetails: DoujinDetails) {
        val fullTitleEnglish = doujinDetails.fullTitleEnglish
        val shortTitleEnglish = doujinDetails.shortTitleEnglish

        // No need to prettify the title if short title does not exist
        if (shortTitleEnglish.isBlank()) {
            txtTitleEng.text = doujinDetails.fullTitleEnglish
            return
        }

        val indexOfShortTitle = fullTitleEnglish.indexOf(shortTitleEnglish)
        val indexAfterShortTitle = indexOfShortTitle + shortTitleEnglish.length

        // If the short title is not found inside full title. Then we don't have to prettify it.
        if (indexOfShortTitle == -1) {
            txtTitleEng.text = doujinDetails.fullTitleEnglish
            return
        }

        val coloredTitle = SpannableString(fullTitleEnglish)
        coloredTitle.apply {
            setSpan(
                StyleSpan(Typeface.BOLD),
                indexOfShortTitle,
                +indexAfterShortTitle,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        }

        txtTitleEng.text = coloredTitle
    }

    private fun openCollectionDialog() {
        val dialog = DialogCollectionList()
        dialog.show(childFragmentManager, COLLECTION_DIALOG_TAG)
    }

    // https://stackoverflow.com/questions/47045788/fragment-declared-target-fragment-that-does-not-belong-to-this-fragmentmanager
    private fun syncMetadata() {
        val dirName = File(viewModel.currentPath).name
        val dialog = DialogSelectSource.newInstance(dirName)
        dialog.setTargetFragment(this, 0) // For passing methods from this fragment to dialog
        dialog.show(requireActivity().supportFragmentManager, DialogSelectSource.name)
    }

    private fun saveActivityInfo() {
        val intent = requireActivity().intent
        ActivityStackUtils.pushNewActivityIntent(
            requireContext(),
            intent,
            ActivityHistory.DoujinDetailsActivity()
        )
    }

    // Fetch tags/titles if details not yet exists. Otherwise, only reset the tags
    override fun onFetchMetadata(sources: EnumSet<Source>) {
        if (viewModel.detailsNotExists()) {
            val dirPath =
                requireActivity().intent.getStringExtra(LocalDoujinsAdapter.DOUJIN_FILE_PATH)
            FetchMetadataService.startService(requireContext(), dirPath, sources)
        } else {
            viewModel.resetTags(sources)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): FragmentDoujinDetails {
            return FragmentDoujinDetails()
        }
    }
}

