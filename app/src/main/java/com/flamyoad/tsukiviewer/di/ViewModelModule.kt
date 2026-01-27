package com.flamyoad.tsukiviewer.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.flamyoad.tsukiviewer.ui.doujinpage.DoujinViewModel
import com.flamyoad.tsukiviewer.ui.editor.EditorViewModel
import com.flamyoad.tsukiviewer.ui.home.bookmarks.BookmarkViewModel
import com.flamyoad.tsukiviewer.ui.home.collections.CollectionViewModel
import com.flamyoad.tsukiviewer.ui.home.collections.CreateCollectionViewModel
import com.flamyoad.tsukiviewer.ui.home.collections.DialogCollectionInfoViewModel
import com.flamyoad.tsukiviewer.ui.home.collections.doujins.CollectionDoujinsViewModel
import com.flamyoad.tsukiviewer.ui.home.local.LocalDoujinViewModel
import com.flamyoad.tsukiviewer.ui.home.tags.DoujinTagsViewModel
import com.flamyoad.tsukiviewer.ui.reader.ReaderViewModel
import com.flamyoad.tsukiviewer.ui.reader.recents.RecentTabsViewModel
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabViewModel
import com.flamyoad.tsukiviewer.ui.screens.bookmarks.BookmarksViewModel
import com.flamyoad.tsukiviewer.ui.screens.collections.CollectionsViewModel
import com.flamyoad.tsukiviewer.ui.screens.doujin.DoujinDetailsViewModel
import com.flamyoad.tsukiviewer.ui.screens.home.HomeViewModel
import com.flamyoad.tsukiviewer.ui.screens.tags.TagsViewModel
import com.flamyoad.tsukiviewer.ui.search.SearchResultViewModel
import com.flamyoad.tsukiviewer.ui.search.SearchViewModel
import com.flamyoad.tsukiviewer.ui.settings.SettingsViewModel
import com.flamyoad.tsukiviewer.ui.settings.includedfolders.IncludedFolderViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(LocalDoujinViewModel::class)
    abstract fun bindLocalDoujinViewModel(viewModel: LocalDoujinViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BookmarkViewModel::class)
    abstract fun bindBookmarkViewModel(viewModel: BookmarkViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CollectionViewModel::class)
    abstract fun bindCollectionViewModel(viewModel: CollectionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateCollectionViewModel::class)
    abstract fun bindCreateCollectionViewModel(viewModel: CreateCollectionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CollectionDoujinsViewModel::class)
    abstract fun bindCollectionDoujinsViewModel(viewModel: CollectionDoujinsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DialogCollectionInfoViewModel::class)
    abstract fun bindDialogCollectionInfoViewModel(viewModel: DialogCollectionInfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DoujinTagsViewModel::class)
    abstract fun bindDoujinTagsViewModel(viewModel: DoujinTagsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DoujinViewModel::class)
    abstract fun bindDoujinViewModel(viewModel: DoujinViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditorViewModel::class)
    abstract fun bindEditorViewModel(viewModel: EditorViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReaderViewModel::class)
    abstract fun bindReaderViewModel(viewModel: ReaderViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReaderTabViewModel::class)
    abstract fun bindReaderTabViewModel(viewModel: ReaderTabViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RecentTabsViewModel::class)
    abstract fun bindRecentTabsViewModel(viewModel: RecentTabsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(viewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchResultViewModel::class)
    abstract fun bindSearchResultViewModel(viewModel: SearchResultViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(IncludedFolderViewModel::class)
    abstract fun bindIncludedFolderViewModel(viewModel: IncludedFolderViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DoujinDetailsViewModel::class)
    abstract fun bindDoujinDetailsViewModel(viewModel: DoujinDetailsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CollectionsViewModel::class)
    abstract fun bindCollectionsViewModel(viewModel: CollectionsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TagsViewModel::class)
    abstract fun bindTagsViewModel(viewModel: TagsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BookmarksViewModel::class)
    abstract fun bindBookmarksViewModel(viewModel: BookmarksViewModel): ViewModel
}
