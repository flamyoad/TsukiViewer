package com.flamyoad.tsukiviewer.core.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u001b2\u00020\u0001:\u0001\u001bB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&J\b\u0010\u000b\u001a\u00020\fH&J\b\u0010\r\u001a\u00020\u000eH&J\b\u0010\u000f\u001a\u00020\u0010H&J\b\u0010\u0011\u001a\u00020\u0012H&J\b\u0010\u0013\u001a\u00020\u0014H&J\b\u0010\u0015\u001a\u00020\u0016H&J\b\u0010\u0017\u001a\u00020\u0018H&J\b\u0010\u0019\u001a\u00020\u001aH&\u00a8\u0006\u001c"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/db/AppDatabase;", "Landroidx/room/RoomDatabase;", "()V", "bookmarkGroupDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/BookmarkGroupDao;", "bookmarkItemDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/BookmarkItemDao;", "collectionCriteriaDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionCriteriaDao;", "collectionDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionDao;", "collectionDoujinDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionDoujinDao;", "doujinDetailsDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/DoujinDetailsDao;", "doujinTagDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/DoujinTagsDao;", "folderDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/IncludedFolderDao;", "includedFolderDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/IncludedPathDao;", "recentTabDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/RecentTabDao;", "searchHistoryDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/SearchHistoryDao;", "tagsDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/TagDao;", "Companion", "core_debug"})
@androidx.room.Database(entities = {com.flamyoad.tsukiviewer.core.model.IncludedPath.class, com.flamyoad.tsukiviewer.core.model.DoujinDetails.class, com.flamyoad.tsukiviewer.core.model.Tag.class, com.flamyoad.tsukiviewer.core.model.DoujinTag.class, com.flamyoad.tsukiviewer.core.model.IncludedFolder.class, com.flamyoad.tsukiviewer.core.model.BookmarkGroup.class, com.flamyoad.tsukiviewer.core.model.BookmarkItem.class, com.flamyoad.tsukiviewer.core.model.SearchHistory.class, com.flamyoad.tsukiviewer.core.model.Collection.class, com.flamyoad.tsukiviewer.core.model.CollectionCriteria.class, com.flamyoad.tsukiviewer.core.model.RecentTab.class}, version = 5)
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.flamyoad.tsukiviewer.core.db.AppDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_1_2 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_2_3 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_3_4 = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_4_5 = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.flamyoad.tsukiviewer.core.db.AppDatabase.Companion Companion = null;
    
    public AppDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao includedFolderDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.flamyoad.tsukiviewer.core.db.dao.DoujinDetailsDao doujinDetailsDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.flamyoad.tsukiviewer.core.db.dao.TagDao tagsDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.flamyoad.tsukiviewer.core.db.dao.DoujinTagsDao doujinTagDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.flamyoad.tsukiviewer.core.db.dao.IncludedFolderDao folderDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.flamyoad.tsukiviewer.core.db.dao.BookmarkGroupDao bookmarkGroupDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.flamyoad.tsukiviewer.core.db.dao.BookmarkItemDao bookmarkItemDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.flamyoad.tsukiviewer.core.db.dao.SearchHistoryDao searchHistoryDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.flamyoad.tsukiviewer.core.db.dao.CollectionDao collectionDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.flamyoad.tsukiviewer.core.db.dao.CollectionCriteriaDao collectionCriteriaDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.flamyoad.tsukiviewer.core.db.dao.CollectionDoujinDao collectionDoujinDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.flamyoad.tsukiviewer.core.db.dao.RecentTabDao recentTabDao();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0010\u001a\u00020\u0011R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\bR\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\bR\u0011\u0010\r\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\b\u00a8\u0006\u0012"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/db/AppDatabase$Companion;", "", "()V", "INSTANCE", "Lcom/flamyoad/tsukiviewer/core/db/AppDatabase;", "MIGRATION_1_2", "Landroidx/room/migration/Migration;", "getMIGRATION_1_2", "()Landroidx/room/migration/Migration;", "MIGRATION_2_3", "getMIGRATION_2_3", "MIGRATION_3_4", "getMIGRATION_3_4", "MIGRATION_4_5", "getMIGRATION_4_5", "getInstance", "context", "Landroid/content/Context;", "core_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.room.migration.Migration getMIGRATION_1_2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.room.migration.Migration getMIGRATION_2_3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.room.migration.Migration getMIGRATION_3_4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.room.migration.Migration getMIGRATION_4_5() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.flamyoad.tsukiviewer.core.db.AppDatabase getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}