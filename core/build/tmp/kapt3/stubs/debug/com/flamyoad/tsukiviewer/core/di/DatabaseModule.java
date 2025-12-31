package com.flamyoad.tsukiviewer.core.di;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0007J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0004H\u0007J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\t\u001a\u00020\u0004H\u0007J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\t\u001a\u00020\u0004H\u0007J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\t\u001a\u00020\u0004H\u0007J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\u0004H\u0007J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\t\u001a\u00020\u0004H\u0007J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\t\u001a\u00020\u0004H\u0007J\u0010\u0010\u0016\u001a\u00020\u00172\u0006\u0010\t\u001a\u00020\u0004H\u0007J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\t\u001a\u00020\u0004H\u0007J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\t\u001a\u00020\u0004H\u0007J\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\t\u001a\u00020\u0004H\u0007J\u0010\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\t\u001a\u00020\u0004H\u0007\u00a8\u0006 "}, d2 = {"Lcom/flamyoad/tsukiviewer/core/di/DatabaseModule;", "", "()V", "provideAppDatabase", "Lcom/flamyoad/tsukiviewer/core/db/AppDatabase;", "context", "Landroid/content/Context;", "provideBookmarkGroupDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/BookmarkGroupDao;", "database", "provideBookmarkItemDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/BookmarkItemDao;", "provideCollectionCriteriaDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionCriteriaDao;", "provideCollectionDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionDao;", "provideCollectionDoujinDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionDoujinDao;", "provideDoujinDetailsDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/DoujinDetailsDao;", "provideDoujinTagDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/DoujinTagsDao;", "provideFolderDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/IncludedFolderDao;", "provideIncludedPathDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/IncludedPathDao;", "provideRecentTabDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/RecentTabDao;", "provideSearchHistoryDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/SearchHistoryDao;", "provideTagsDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/TagDao;", "core_debug"})
public final class DatabaseModule {
    
    public DatabaseModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.AppDatabase provideAppDatabase(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao provideIncludedPathDao(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.DoujinDetailsDao provideDoujinDetailsDao(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.TagDao provideTagsDao(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.DoujinTagsDao provideDoujinTagDao(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.IncludedFolderDao provideFolderDao(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.BookmarkGroupDao provideBookmarkGroupDao(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.BookmarkItemDao provideBookmarkItemDao(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.SearchHistoryDao provideSearchHistoryDao(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.CollectionDao provideCollectionDao(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.CollectionCriteriaDao provideCollectionCriteriaDao(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.CollectionDoujinDao provideCollectionDoujinDao(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase database) {
        return null;
    }
    
    @dagger.Provides()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.RecentTabDao provideRecentTabDao(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase database) {
        return null;
    }
}