package com.flamyoad.tsukiviewer.ui.reader.recents

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.adapter.RecentTabsAdapter
import com.flamyoad.tsukiviewer.core.model.RecentTab
import com.flamyoad.tsukiviewer.databinding.ActivityRecentTabsBinding
import com.flamyoad.tsukiviewer.di.ViewModelFactory
import com.flamyoad.tsukiviewer.security.LockableActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecentTabsActivity : LockableActivity() {

    @Inject lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: RecentTabsViewModel by viewModels { viewModelFactory }
    private lateinit var binding: ActivityRecentTabsBinding
    private lateinit var tabAdapter: RecentTabsAdapter
    private lateinit var grid: GridLayoutManager
    private var activeTabId: Long? = null
    private var revealActiveTabOnLoad = true
    private var changingTabs = false
    private var undoBar: Snackbar? = null
    private var libraryPicker: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityRecentTabsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val initialId = savedInstanceState?.getLong(CHOSEN_TAB_ID, NO_TAB)
            ?: intent.getLongExtra(ACTIVE_TAB_ID, NO_TAB)
        activeTabId = initialId.takeUnless { it == NO_TAB }
        revealActiveTabOnLoad = savedInstanceState == null

        tabAdapter = RecentTabsAdapter(::openCard, ::closeTab)
        grid = GridLayoutManager(this, (resources.configuration.screenWidthDp / 176).coerceIn(2, 6))
        binding.listTabs.layoutManager = grid
        binding.listTabs.adapter = tabAdapter
        ItemTouchHelper(RecentTabTouchHelperCallback(tabAdapter, ::closeTab) { !changingTabs })
            .attachToRecyclerView(binding.listTabs)

        binding.toolbar.apply {
            setNavigationContentDescription(R.string.reader_tabs_back)
            setNavigationOnClickListener { onBackPressed() }
            inflateMenu(R.menu.reader_tabs)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.closeOtherTabs -> closeTabs(activeTabId)
                    R.id.closeAllTabs -> closeTabs(null)
                    else -> return@setOnMenuItemClickListener false
                }
                true
            }
        }
        binding.openBook.setOnClickListener { showLibraryPicker() }
        updateToolbar()
        viewModel.tabList.observe(this, ::showTabs)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong(CHOSEN_TAB_ID, activeTabId ?: NO_TAB)
        super.onSaveInstanceState(outState)
    }

    override fun onStop() {
        libraryPicker?.dismiss()
        super.onStop()
    }

    override fun onBackPressed() {
        if (!changingTabs) returnToReader(activeTabId)
    }

    private fun openCard(tab: RecentTab) {
        if (!changingTabs && !appLock.isLocked) returnToReader(tab.id)
    }

    private fun showTabs(tabs: List<RecentTab>) {
        if ((!revealActiveTabOnLoad || tabs.isNotEmpty()) && tabs.none { it.id == activeTabId }) {
            val previousIndex = tabAdapter.currentList.indexOfFirst { it.id == activeTabId }
            activeTabId = tabs.getOrNull(previousIndex.coerceAtMost(tabs.lastIndex))?.id
                ?: tabs.firstOrNull()?.id
        }
        tabAdapter.activeTabId = activeTabId
        tabAdapter.submitList(tabs) {
            if (revealActiveTabOnLoad && revealActiveTab()) {
                revealActiveTabOnLoad = false
                binding.listTabs.doOnPreDraw {
                    val card = activeTabId?.let(binding.listTabs::findViewHolderForItemId)?.itemView
                    if (card == null || !grid.isViewPartiallyVisible(card, true, true)) {
                        revealActiveTab()
                    }
                }
            }
            binding.emptyTabs.isVisible = tabs.isEmpty()
            updateToolbar()
        }
    }

    private fun revealActiveTab(): Boolean {
        val position = tabAdapter.currentList.indexOfFirst { it.id == activeTabId }
        if (position < 0) return false
        // A row ending exactly at the top padding can become the next layout anchor.
        // Move that boundary by one pixel so later thumbnail layouts keep the active row in place.
        grid.scrollToPositionWithOffset(position, -1)
        return true
    }

    private fun updateToolbar() {
        val count = tabAdapter.itemCount
        binding.toolbar.title = getString(R.string.reader_tabs_count, count)
        binding.toolbar.menu.findItem(R.id.closeOtherTabs).isEnabled =
            !changingTabs && activeTabId != null && count > 1
        binding.toolbar.menu.findItem(R.id.closeAllTabs).isEnabled = !changingTabs && count > 0
        binding.openBook.isEnabled = !changingTabs
    }

    private fun closeTab(tab: RecentTab) {
        changeTabs(onComplete = {
            // A failed swipe has already moved the holder out of the grid.
            val position = tabAdapter.currentList.indexOf(tab)
            if (position >= 0 && viewModel.tabList.value?.contains(tab) == true) {
                tabAdapter.notifyItemChanged(position)
            }
        }) {
            if (!viewModel.close(tab)) return@changeTabs
            if (activeTabId == tab.id) {
                val tabs = tabAdapter.currentList
                val index = tabs.indexOf(tab)
                activeTabId = (tabs.getOrNull(index + 1) ?: tabs.getOrNull(index - 1))?.id
                tabAdapter.activeTabId = activeTabId
            }
            undoBar?.dismiss()
            undoBar = Snackbar.make(binding.root, R.string.reader_tabs_closed, Snackbar.LENGTH_LONG)
                .setAction(R.string.reader_tabs_undo) {
                    changeTabs {
                        val id = viewModel.restore(tab)
                        if (activeTabId == null) {
                            activeTabId = id
                            tabAdapter.activeTabId = id
                        }
                    }
                }
            undoBar?.show()
        }
    }

    private fun closeTabs(keepTabId: Long?) {
        changeTabs {
            viewModel.closeTabs(keepTabId)
            activeTabId = keepTabId
            tabAdapter.activeTabId = keepTabId
            undoBar?.dismiss()
        }
    }

    private fun showLibraryPicker() {
        if (changingTabs || libraryPicker != null || appLock.isLocked) return
        val padding = (16 * resources.displayMetrics.density).toInt()
        val search = TextInputEditText(this).apply {
            setSingleLine()
            hint = getString(R.string.reader_tabs_search)
        }
        val status = TextView(this).apply {
            setPadding(0, padding, 0, padding)
            setText(R.string.reader_tabs_loading)
        }
        val choices = ListView(this).apply {
            emptyView = status
        }
        val entries = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        choices.adapter = entries
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(padding, 0, padding, padding)
            addView(search)
            addView(status)
            addView(choices)
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.reader_tabs_open_book)
            .setView(content)
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        var books = emptyList<RecentTab>()
        var filtered = emptyList<RecentTab>()
        fun filter() {
            val query = search.text.toString().trim()
            filtered = books.filter { it.title.contains(query, ignoreCase = true) }
            entries.clear()
            entries.addAll(filtered.map { it.title })
        }
        search.doAfterTextChanged { filter() }
        choices.setOnItemClickListener { _, _, position, _ ->
            val book = filtered.getOrNull(position) ?: return@setOnItemClickListener
            dialog.dismiss()
            changeTabs {
                val id = viewModel.open(book)
                if (id == null) {
                    Snackbar.make(binding.root, R.string.reader_tabs_book_unavailable, Snackbar.LENGTH_LONG).show()
                } else {
                    returnToReader(id)
                }
            }
        }
        if (appLock.enabled.value) {
            dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
        libraryPicker = dialog
        dialog.show()
        val load = lifecycleScope.launch {
            try {
                books = viewModel.loadLibrary()
                status.setText(R.string.reader_tabs_no_books)
                filter()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                status.setText(R.string.reader_tabs_error)
            }
        }
        dialog.setOnDismissListener {
            libraryPicker = null
            load.cancel()
        }
    }

    private fun changeTabs(onComplete: () -> Unit = {}, action: suspend () -> Unit) {
        if (changingTabs || appLock.isLocked) return
        changingTabs = true
        updateToolbar()
        lifecycleScope.launch {
            try {
                action()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Snackbar.make(binding.root, R.string.reader_tabs_error, Snackbar.LENGTH_LONG).show()
            } finally {
                changingTabs = false
                onComplete()
                updateToolbar()
            }
        }
    }

    private fun returnToReader(tabId: Long?) {
        setResult(RESULT_OK, Intent().putExtra(CHOSEN_TAB_ID, tabId ?: NO_TAB))
        if (Build.VERSION.SDK_INT >= 34) {
            val animation = R.anim.reader_no_window_animation
            overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, animation, animation)
        }
        finish()
        if (Build.VERSION.SDK_INT < 34) overridePendingTransition(0, 0)
    }

    companion object {
        const val ACTIVE_TAB_ID = "tab_id"
        const val CHOSEN_TAB_ID = "chosen_tab_id"
        const val NO_TAB = -1L
    }
}
