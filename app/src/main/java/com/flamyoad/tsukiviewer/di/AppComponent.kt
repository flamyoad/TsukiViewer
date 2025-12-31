package com.flamyoad.tsukiviewer.di

import android.app.Application
import com.flamyoad.tsukiviewer.MainActivity
import com.flamyoad.tsukiviewer.MyApplication
import com.flamyoad.tsukiviewer.core.repository.MetadataRepository
import com.flamyoad.tsukiviewer.ui.doujinpage.DialogCollectionList
import com.flamyoad.tsukiviewer.ui.doujinpage.DialogRemoveMetadata
import com.flamyoad.tsukiviewer.ui.doujinpage.DialogViewStyle
import com.flamyoad.tsukiviewer.ui.doujinpage.DoujinDetailsActivity
import com.flamyoad.tsukiviewer.ui.doujinpage.FragmentDoujinDetails
import com.flamyoad.tsukiviewer.ui.doujinpage.FragmentGridImages
import com.flamyoad.tsukiviewer.ui.editor.EditorActivity
import com.flamyoad.tsukiviewer.ui.editor.TagBottomSheetDialog
import com.flamyoad.tsukiviewer.ui.fetcher.FetcherStatusActivity
import com.flamyoad.tsukiviewer.ui.home.bookmarks.BookmarkFragment
import com.flamyoad.tsukiviewer.ui.home.bookmarks.DialogChangeGroupName
import com.flamyoad.tsukiviewer.ui.home.bookmarks.DialogDeleteItems
import com.flamyoad.tsukiviewer.ui.home.bookmarks.DialogNewGroup
import com.flamyoad.tsukiviewer.ui.home.collections.CollectionFragment
import com.flamyoad.tsukiviewer.ui.home.collections.CreateCollectionActivity
import com.flamyoad.tsukiviewer.ui.home.collections.DialogCollectionInfo
import com.flamyoad.tsukiviewer.ui.home.collections.DialogCollectionViewStyle
import com.flamyoad.tsukiviewer.ui.home.collections.DialogDirectoryPicker
import com.flamyoad.tsukiviewer.ui.home.collections.DialogTagPicker
import com.flamyoad.tsukiviewer.ui.home.collections.doujins.BookmarkGroupDialog
import com.flamyoad.tsukiviewer.ui.home.collections.doujins.CollectionDoujinsActivity
import com.flamyoad.tsukiviewer.ui.home.local.DialogSortDoujin
import com.flamyoad.tsukiviewer.ui.home.local.LocalDoujinsFragment
import com.flamyoad.tsukiviewer.ui.home.tags.DoujinTagsFragment
import com.flamyoad.tsukiviewer.ui.home.tags.TagFragment
import com.flamyoad.tsukiviewer.ui.reader.ReaderActivity
import com.flamyoad.tsukiviewer.ui.reader.recents.RecentTabsActivity
import com.flamyoad.tsukiviewer.ui.reader.tabs.ReaderTabFragment
import com.flamyoad.tsukiviewer.ui.search.SearchActivity
import com.flamyoad.tsukiviewer.ui.search.SearchResultActivity
import com.flamyoad.tsukiviewer.ui.settings.SettingsActivity
import com.flamyoad.tsukiviewer.ui.settings.includedfolders.IncludedFolderActivity
import com.flamyoad.tsukiviewer.core.di.DatabaseModule
import com.flamyoad.tsukiviewer.core.di.RepositoryModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        DatabaseModule::class,
        RepositoryModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {

    fun application(): Application
    
    // Exposed dependencies
    fun metadataRepository(): MetadataRepository

    // Activities
    fun inject(activity: MainActivity)
    fun inject(activity: DoujinDetailsActivity)
    fun inject(activity: EditorActivity)
    fun inject(activity: FetcherStatusActivity)
    fun inject(activity: CreateCollectionActivity)
    fun inject(activity: CollectionDoujinsActivity)
    fun inject(activity: ReaderActivity)
    fun inject(activity: RecentTabsActivity)
    fun inject(activity: SearchActivity)
    fun inject(activity: SearchResultActivity)
    fun inject(activity: SettingsActivity)
    fun inject(activity: IncludedFolderActivity)

    // Fragments
    fun inject(fragment: LocalDoujinsFragment)
    fun inject(fragment: BookmarkFragment)
    fun inject(fragment: CollectionFragment)
    fun inject(fragment: DoujinTagsFragment)
    fun inject(fragment: TagFragment)
    fun inject(fragment: FragmentDoujinDetails)
    fun inject(fragment: FragmentGridImages)
    fun inject(fragment: ReaderTabFragment)

    // Dialogs
    fun inject(dialog: TagBottomSheetDialog)
    fun inject(dialog: DialogCollectionInfo)
    fun inject(dialog: DialogViewStyle)
    fun inject(dialog: DialogCollectionList)
    fun inject(dialog: DialogRemoveMetadata)
    fun inject(dialog: DialogTagPicker)
    fun inject(dialog: DialogDirectoryPicker)
    fun inject(dialog: DialogCollectionViewStyle)
    fun inject(dialog: BookmarkGroupDialog)
    fun inject(dialog: DialogNewGroup)
    fun inject(dialog: DialogDeleteItems)
    fun inject(dialog: DialogChangeGroupName)
    fun inject(dialog: DialogSortDoujin)
    fun inject(dialog: com.flamyoad.tsukiviewer.ui.home.local.DialogNewGroup)
    fun inject(dialog: com.flamyoad.tsukiviewer.ui.search.BookmarkGroupDialog)

    // Application
    fun inject(application: MyApplication)
}
