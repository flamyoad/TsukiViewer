package com.flamyoad.tsukiviewer.core.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.flamyoad.tsukiviewer.core.db.dao.BookmarkGroupDao;
import com.flamyoad.tsukiviewer.core.db.dao.BookmarkGroupDao_Impl;
import com.flamyoad.tsukiviewer.core.db.dao.BookmarkItemDao;
import com.flamyoad.tsukiviewer.core.db.dao.BookmarkItemDao_Impl;
import com.flamyoad.tsukiviewer.core.db.dao.CollectionCriteriaDao;
import com.flamyoad.tsukiviewer.core.db.dao.CollectionCriteriaDao_Impl;
import com.flamyoad.tsukiviewer.core.db.dao.CollectionDao;
import com.flamyoad.tsukiviewer.core.db.dao.CollectionDao_Impl;
import com.flamyoad.tsukiviewer.core.db.dao.CollectionDoujinDao;
import com.flamyoad.tsukiviewer.core.db.dao.CollectionDoujinDao_Impl;
import com.flamyoad.tsukiviewer.core.db.dao.DoujinDetailsDao;
import com.flamyoad.tsukiviewer.core.db.dao.DoujinDetailsDao_Impl;
import com.flamyoad.tsukiviewer.core.db.dao.DoujinTagsDao;
import com.flamyoad.tsukiviewer.core.db.dao.DoujinTagsDao_Impl;
import com.flamyoad.tsukiviewer.core.db.dao.IncludedFolderDao;
import com.flamyoad.tsukiviewer.core.db.dao.IncludedFolderDao_Impl;
import com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao;
import com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao_Impl;
import com.flamyoad.tsukiviewer.core.db.dao.RecentTabDao;
import com.flamyoad.tsukiviewer.core.db.dao.RecentTabDao_Impl;
import com.flamyoad.tsukiviewer.core.db.dao.SearchHistoryDao;
import com.flamyoad.tsukiviewer.core.db.dao.SearchHistoryDao_Impl;
import com.flamyoad.tsukiviewer.core.db.dao.TagDao;
import com.flamyoad.tsukiviewer.core.db.dao.TagDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile IncludedPathDao _includedPathDao;

  private volatile DoujinDetailsDao _doujinDetailsDao;

  private volatile TagDao _tagDao;

  private volatile DoujinTagsDao _doujinTagsDao;

  private volatile IncludedFolderDao _includedFolderDao;

  private volatile BookmarkGroupDao _bookmarkGroupDao;

  private volatile BookmarkItemDao _bookmarkItemDao;

  private volatile SearchHistoryDao _searchHistoryDao;

  private volatile CollectionDao _collectionDao;

  private volatile CollectionCriteriaDao _collectionCriteriaDao;

  private volatile CollectionDoujinDao _collectionDoujinDao;

  private volatile RecentTabDao _recentTabDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(5) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `included_path` (`dir` TEXT NOT NULL, PRIMARY KEY(`dir`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `doujin_details` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `nukeCode` INTEGER NOT NULL, `fullTitleEnglish` TEXT NOT NULL, `fullTitleJapanese` TEXT NOT NULL, `shortTitleEnglish` TEXT NOT NULL, `absolutePath` TEXT NOT NULL, `folderName` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tags` (`tagId` INTEGER PRIMARY KEY AUTOINCREMENT, `type` TEXT NOT NULL, `name` TEXT NOT NULL, `url` TEXT NOT NULL, `count` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `doujin_tags` (`doujinId` INTEGER NOT NULL, `tagId` INTEGER NOT NULL, PRIMARY KEY(`doujinId`, `tagId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_doujin_tags_doujinId_tagId` ON `doujin_tags` (`doujinId`, `tagId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `included_folders` (`dir` TEXT NOT NULL, `parentDir` TEXT NOT NULL, `lastName` TEXT NOT NULL, PRIMARY KEY(`dir`), FOREIGN KEY(`parentDir`) REFERENCES `included_path`(`dir`) ON UPDATE NO ACTION ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bookmark_group` (`name` TEXT NOT NULL, PRIMARY KEY(`name`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bookmark_item` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `absolutePath` TEXT NOT NULL, `parentName` TEXT NOT NULL, `dateAdded` INTEGER NOT NULL, FOREIGN KEY(`parentName`) REFERENCES `bookmark_group`(`name`) ON UPDATE CASCADE ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `search_history` (`id` INTEGER, `title` TEXT NOT NULL, `tags` TEXT NOT NULL, `mustIncludeAllTags` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `collection` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL, `coverPhoto` TEXT NOT NULL, `mustHaveAllTitles` INTEGER NOT NULL, `mustHaveAllIncludedTags` INTEGER NOT NULL, `mustHaveAllExcludedTags` INTEGER NOT NULL, `minNumPages` INTEGER NOT NULL, `maxNumPages` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `collection_criteria` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `collectionId` INTEGER NOT NULL, `type` TEXT NOT NULL, `value` TEXT NOT NULL, `valueName` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `recent_tabs` (`id` INTEGER, `title` TEXT NOT NULL, `dirPath` TEXT NOT NULL, `thumbnail` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '5b855660336b8dc6a105a53d4b2c5ffa')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `included_path`");
        db.execSQL("DROP TABLE IF EXISTS `doujin_details`");
        db.execSQL("DROP TABLE IF EXISTS `tags`");
        db.execSQL("DROP TABLE IF EXISTS `doujin_tags`");
        db.execSQL("DROP TABLE IF EXISTS `included_folders`");
        db.execSQL("DROP TABLE IF EXISTS `bookmark_group`");
        db.execSQL("DROP TABLE IF EXISTS `bookmark_item`");
        db.execSQL("DROP TABLE IF EXISTS `search_history`");
        db.execSQL("DROP TABLE IF EXISTS `collection`");
        db.execSQL("DROP TABLE IF EXISTS `collection_criteria`");
        db.execSQL("DROP TABLE IF EXISTS `recent_tabs`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsIncludedPath = new HashMap<String, TableInfo.Column>(1);
        _columnsIncludedPath.put("dir", new TableInfo.Column("dir", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIncludedPath = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesIncludedPath = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoIncludedPath = new TableInfo("included_path", _columnsIncludedPath, _foreignKeysIncludedPath, _indicesIncludedPath);
        final TableInfo _existingIncludedPath = TableInfo.read(db, "included_path");
        if (!_infoIncludedPath.equals(_existingIncludedPath)) {
          return new RoomOpenHelper.ValidationResult(false, "included_path(com.flamyoad.tsukiviewer.core.model.IncludedPath).\n"
                  + " Expected:\n" + _infoIncludedPath + "\n"
                  + " Found:\n" + _existingIncludedPath);
        }
        final HashMap<String, TableInfo.Column> _columnsDoujinDetails = new HashMap<String, TableInfo.Column>(7);
        _columnsDoujinDetails.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDoujinDetails.put("nukeCode", new TableInfo.Column("nukeCode", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDoujinDetails.put("fullTitleEnglish", new TableInfo.Column("fullTitleEnglish", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDoujinDetails.put("fullTitleJapanese", new TableInfo.Column("fullTitleJapanese", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDoujinDetails.put("shortTitleEnglish", new TableInfo.Column("shortTitleEnglish", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDoujinDetails.put("absolutePath", new TableInfo.Column("absolutePath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDoujinDetails.put("folderName", new TableInfo.Column("folderName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDoujinDetails = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDoujinDetails = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDoujinDetails = new TableInfo("doujin_details", _columnsDoujinDetails, _foreignKeysDoujinDetails, _indicesDoujinDetails);
        final TableInfo _existingDoujinDetails = TableInfo.read(db, "doujin_details");
        if (!_infoDoujinDetails.equals(_existingDoujinDetails)) {
          return new RoomOpenHelper.ValidationResult(false, "doujin_details(com.flamyoad.tsukiviewer.core.model.DoujinDetails).\n"
                  + " Expected:\n" + _infoDoujinDetails + "\n"
                  + " Found:\n" + _existingDoujinDetails);
        }
        final HashMap<String, TableInfo.Column> _columnsTags = new HashMap<String, TableInfo.Column>(5);
        _columnsTags.put("tagId", new TableInfo.Column("tagId", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("url", new TableInfo.Column("url", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("count", new TableInfo.Column("count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTags = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTags = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTags = new TableInfo("tags", _columnsTags, _foreignKeysTags, _indicesTags);
        final TableInfo _existingTags = TableInfo.read(db, "tags");
        if (!_infoTags.equals(_existingTags)) {
          return new RoomOpenHelper.ValidationResult(false, "tags(com.flamyoad.tsukiviewer.core.model.Tag).\n"
                  + " Expected:\n" + _infoTags + "\n"
                  + " Found:\n" + _existingTags);
        }
        final HashMap<String, TableInfo.Column> _columnsDoujinTags = new HashMap<String, TableInfo.Column>(2);
        _columnsDoujinTags.put("doujinId", new TableInfo.Column("doujinId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDoujinTags.put("tagId", new TableInfo.Column("tagId", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDoujinTags = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDoujinTags = new HashSet<TableInfo.Index>(1);
        _indicesDoujinTags.add(new TableInfo.Index("index_doujin_tags_doujinId_tagId", false, Arrays.asList("doujinId", "tagId"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoDoujinTags = new TableInfo("doujin_tags", _columnsDoujinTags, _foreignKeysDoujinTags, _indicesDoujinTags);
        final TableInfo _existingDoujinTags = TableInfo.read(db, "doujin_tags");
        if (!_infoDoujinTags.equals(_existingDoujinTags)) {
          return new RoomOpenHelper.ValidationResult(false, "doujin_tags(com.flamyoad.tsukiviewer.core.model.DoujinTag).\n"
                  + " Expected:\n" + _infoDoujinTags + "\n"
                  + " Found:\n" + _existingDoujinTags);
        }
        final HashMap<String, TableInfo.Column> _columnsIncludedFolders = new HashMap<String, TableInfo.Column>(3);
        _columnsIncludedFolders.put("dir", new TableInfo.Column("dir", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncludedFolders.put("parentDir", new TableInfo.Column("parentDir", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIncludedFolders.put("lastName", new TableInfo.Column("lastName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIncludedFolders = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysIncludedFolders.add(new TableInfo.ForeignKey("included_path", "CASCADE", "NO ACTION", Arrays.asList("parentDir"), Arrays.asList("dir")));
        final HashSet<TableInfo.Index> _indicesIncludedFolders = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoIncludedFolders = new TableInfo("included_folders", _columnsIncludedFolders, _foreignKeysIncludedFolders, _indicesIncludedFolders);
        final TableInfo _existingIncludedFolders = TableInfo.read(db, "included_folders");
        if (!_infoIncludedFolders.equals(_existingIncludedFolders)) {
          return new RoomOpenHelper.ValidationResult(false, "included_folders(com.flamyoad.tsukiviewer.core.model.IncludedFolder).\n"
                  + " Expected:\n" + _infoIncludedFolders + "\n"
                  + " Found:\n" + _existingIncludedFolders);
        }
        final HashMap<String, TableInfo.Column> _columnsBookmarkGroup = new HashMap<String, TableInfo.Column>(1);
        _columnsBookmarkGroup.put("name", new TableInfo.Column("name", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBookmarkGroup = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBookmarkGroup = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBookmarkGroup = new TableInfo("bookmark_group", _columnsBookmarkGroup, _foreignKeysBookmarkGroup, _indicesBookmarkGroup);
        final TableInfo _existingBookmarkGroup = TableInfo.read(db, "bookmark_group");
        if (!_infoBookmarkGroup.equals(_existingBookmarkGroup)) {
          return new RoomOpenHelper.ValidationResult(false, "bookmark_group(com.flamyoad.tsukiviewer.core.model.BookmarkGroup).\n"
                  + " Expected:\n" + _infoBookmarkGroup + "\n"
                  + " Found:\n" + _existingBookmarkGroup);
        }
        final HashMap<String, TableInfo.Column> _columnsBookmarkItem = new HashMap<String, TableInfo.Column>(4);
        _columnsBookmarkItem.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarkItem.put("absolutePath", new TableInfo.Column("absolutePath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarkItem.put("parentName", new TableInfo.Column("parentName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarkItem.put("dateAdded", new TableInfo.Column("dateAdded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBookmarkItem = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysBookmarkItem.add(new TableInfo.ForeignKey("bookmark_group", "CASCADE", "CASCADE", Arrays.asList("parentName"), Arrays.asList("name")));
        final HashSet<TableInfo.Index> _indicesBookmarkItem = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBookmarkItem = new TableInfo("bookmark_item", _columnsBookmarkItem, _foreignKeysBookmarkItem, _indicesBookmarkItem);
        final TableInfo _existingBookmarkItem = TableInfo.read(db, "bookmark_item");
        if (!_infoBookmarkItem.equals(_existingBookmarkItem)) {
          return new RoomOpenHelper.ValidationResult(false, "bookmark_item(com.flamyoad.tsukiviewer.core.model.BookmarkItem).\n"
                  + " Expected:\n" + _infoBookmarkItem + "\n"
                  + " Found:\n" + _existingBookmarkItem);
        }
        final HashMap<String, TableInfo.Column> _columnsSearchHistory = new HashMap<String, TableInfo.Column>(4);
        _columnsSearchHistory.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("tags", new TableInfo.Column("tags", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSearchHistory.put("mustIncludeAllTags", new TableInfo.Column("mustIncludeAllTags", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSearchHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSearchHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSearchHistory = new TableInfo("search_history", _columnsSearchHistory, _foreignKeysSearchHistory, _indicesSearchHistory);
        final TableInfo _existingSearchHistory = TableInfo.read(db, "search_history");
        if (!_infoSearchHistory.equals(_existingSearchHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "search_history(com.flamyoad.tsukiviewer.core.model.SearchHistory).\n"
                  + " Expected:\n" + _infoSearchHistory + "\n"
                  + " Found:\n" + _existingSearchHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsCollection = new HashMap<String, TableInfo.Column>(8);
        _columnsCollection.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollection.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollection.put("coverPhoto", new TableInfo.Column("coverPhoto", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollection.put("mustHaveAllTitles", new TableInfo.Column("mustHaveAllTitles", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollection.put("mustHaveAllIncludedTags", new TableInfo.Column("mustHaveAllIncludedTags", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollection.put("mustHaveAllExcludedTags", new TableInfo.Column("mustHaveAllExcludedTags", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollection.put("minNumPages", new TableInfo.Column("minNumPages", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollection.put("maxNumPages", new TableInfo.Column("maxNumPages", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCollection = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCollection = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCollection = new TableInfo("collection", _columnsCollection, _foreignKeysCollection, _indicesCollection);
        final TableInfo _existingCollection = TableInfo.read(db, "collection");
        if (!_infoCollection.equals(_existingCollection)) {
          return new RoomOpenHelper.ValidationResult(false, "collection(com.flamyoad.tsukiviewer.core.model.Collection).\n"
                  + " Expected:\n" + _infoCollection + "\n"
                  + " Found:\n" + _existingCollection);
        }
        final HashMap<String, TableInfo.Column> _columnsCollectionCriteria = new HashMap<String, TableInfo.Column>(5);
        _columnsCollectionCriteria.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionCriteria.put("collectionId", new TableInfo.Column("collectionId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionCriteria.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionCriteria.put("value", new TableInfo.Column("value", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCollectionCriteria.put("valueName", new TableInfo.Column("valueName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCollectionCriteria = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCollectionCriteria = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCollectionCriteria = new TableInfo("collection_criteria", _columnsCollectionCriteria, _foreignKeysCollectionCriteria, _indicesCollectionCriteria);
        final TableInfo _existingCollectionCriteria = TableInfo.read(db, "collection_criteria");
        if (!_infoCollectionCriteria.equals(_existingCollectionCriteria)) {
          return new RoomOpenHelper.ValidationResult(false, "collection_criteria(com.flamyoad.tsukiviewer.core.model.CollectionCriteria).\n"
                  + " Expected:\n" + _infoCollectionCriteria + "\n"
                  + " Found:\n" + _existingCollectionCriteria);
        }
        final HashMap<String, TableInfo.Column> _columnsRecentTabs = new HashMap<String, TableInfo.Column>(4);
        _columnsRecentTabs.put("id", new TableInfo.Column("id", "INTEGER", false, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentTabs.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentTabs.put("dirPath", new TableInfo.Column("dirPath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentTabs.put("thumbnail", new TableInfo.Column("thumbnail", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecentTabs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecentTabs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRecentTabs = new TableInfo("recent_tabs", _columnsRecentTabs, _foreignKeysRecentTabs, _indicesRecentTabs);
        final TableInfo _existingRecentTabs = TableInfo.read(db, "recent_tabs");
        if (!_infoRecentTabs.equals(_existingRecentTabs)) {
          return new RoomOpenHelper.ValidationResult(false, "recent_tabs(com.flamyoad.tsukiviewer.core.model.RecentTab).\n"
                  + " Expected:\n" + _infoRecentTabs + "\n"
                  + " Found:\n" + _existingRecentTabs);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "5b855660336b8dc6a105a53d4b2c5ffa", "8bcfd7f4e6dd3c9adffe8ce7bccac20c");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "included_path","doujin_details","tags","doujin_tags","included_folders","bookmark_group","bookmark_item","search_history","collection","collection_criteria","recent_tabs");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `included_path`");
      _db.execSQL("DELETE FROM `doujin_details`");
      _db.execSQL("DELETE FROM `tags`");
      _db.execSQL("DELETE FROM `doujin_tags`");
      _db.execSQL("DELETE FROM `included_folders`");
      _db.execSQL("DELETE FROM `bookmark_group`");
      _db.execSQL("DELETE FROM `bookmark_item`");
      _db.execSQL("DELETE FROM `search_history`");
      _db.execSQL("DELETE FROM `collection`");
      _db.execSQL("DELETE FROM `collection_criteria`");
      _db.execSQL("DELETE FROM `recent_tabs`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(IncludedPathDao.class, IncludedPathDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DoujinDetailsDao.class, DoujinDetailsDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TagDao.class, TagDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DoujinTagsDao.class, DoujinTagsDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IncludedFolderDao.class, IncludedFolderDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BookmarkGroupDao.class, BookmarkGroupDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BookmarkItemDao.class, BookmarkItemDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SearchHistoryDao.class, SearchHistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CollectionDao.class, CollectionDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CollectionCriteriaDao.class, CollectionCriteriaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CollectionDoujinDao.class, CollectionDoujinDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RecentTabDao.class, RecentTabDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public IncludedPathDao includedFolderDao() {
    if (_includedPathDao != null) {
      return _includedPathDao;
    } else {
      synchronized(this) {
        if(_includedPathDao == null) {
          _includedPathDao = new IncludedPathDao_Impl(this);
        }
        return _includedPathDao;
      }
    }
  }

  @Override
  public DoujinDetailsDao doujinDetailsDao() {
    if (_doujinDetailsDao != null) {
      return _doujinDetailsDao;
    } else {
      synchronized(this) {
        if(_doujinDetailsDao == null) {
          _doujinDetailsDao = new DoujinDetailsDao_Impl(this);
        }
        return _doujinDetailsDao;
      }
    }
  }

  @Override
  public TagDao tagsDao() {
    if (_tagDao != null) {
      return _tagDao;
    } else {
      synchronized(this) {
        if(_tagDao == null) {
          _tagDao = new TagDao_Impl(this);
        }
        return _tagDao;
      }
    }
  }

  @Override
  public DoujinTagsDao doujinTagDao() {
    if (_doujinTagsDao != null) {
      return _doujinTagsDao;
    } else {
      synchronized(this) {
        if(_doujinTagsDao == null) {
          _doujinTagsDao = new DoujinTagsDao_Impl(this);
        }
        return _doujinTagsDao;
      }
    }
  }

  @Override
  public IncludedFolderDao folderDao() {
    if (_includedFolderDao != null) {
      return _includedFolderDao;
    } else {
      synchronized(this) {
        if(_includedFolderDao == null) {
          _includedFolderDao = new IncludedFolderDao_Impl(this);
        }
        return _includedFolderDao;
      }
    }
  }

  @Override
  public BookmarkGroupDao bookmarkGroupDao() {
    if (_bookmarkGroupDao != null) {
      return _bookmarkGroupDao;
    } else {
      synchronized(this) {
        if(_bookmarkGroupDao == null) {
          _bookmarkGroupDao = new BookmarkGroupDao_Impl(this);
        }
        return _bookmarkGroupDao;
      }
    }
  }

  @Override
  public BookmarkItemDao bookmarkItemDao() {
    if (_bookmarkItemDao != null) {
      return _bookmarkItemDao;
    } else {
      synchronized(this) {
        if(_bookmarkItemDao == null) {
          _bookmarkItemDao = new BookmarkItemDao_Impl(this);
        }
        return _bookmarkItemDao;
      }
    }
  }

  @Override
  public SearchHistoryDao searchHistoryDao() {
    if (_searchHistoryDao != null) {
      return _searchHistoryDao;
    } else {
      synchronized(this) {
        if(_searchHistoryDao == null) {
          _searchHistoryDao = new SearchHistoryDao_Impl(this);
        }
        return _searchHistoryDao;
      }
    }
  }

  @Override
  public CollectionDao collectionDao() {
    if (_collectionDao != null) {
      return _collectionDao;
    } else {
      synchronized(this) {
        if(_collectionDao == null) {
          _collectionDao = new CollectionDao_Impl(this);
        }
        return _collectionDao;
      }
    }
  }

  @Override
  public CollectionCriteriaDao collectionCriteriaDao() {
    if (_collectionCriteriaDao != null) {
      return _collectionCriteriaDao;
    } else {
      synchronized(this) {
        if(_collectionCriteriaDao == null) {
          _collectionCriteriaDao = new CollectionCriteriaDao_Impl(this);
        }
        return _collectionCriteriaDao;
      }
    }
  }

  @Override
  public CollectionDoujinDao collectionDoujinDao() {
    if (_collectionDoujinDao != null) {
      return _collectionDoujinDao;
    } else {
      synchronized(this) {
        if(_collectionDoujinDao == null) {
          _collectionDoujinDao = new CollectionDoujinDao_Impl(this);
        }
        return _collectionDoujinDao;
      }
    }
  }

  @Override
  public RecentTabDao recentTabDao() {
    if (_recentTabDao != null) {
      return _recentTabDao;
    } else {
      synchronized(this) {
        if(_recentTabDao == null) {
          _recentTabDao = new RecentTabDao_Impl(this);
        }
        return _recentTabDao;
      }
    }
  }
}
