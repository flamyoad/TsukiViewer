package com.flamyoad.tsukiviewer.core.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001e\u0010\u0002\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001c\u0010\u0002\u001a\u00020\u00072\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00050\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0010\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u001e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001c\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u000e0\u00172\u0006\u0010\n\u001a\u00020\u000bH\'J\u0016\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\"\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u000e2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00050\u000eH\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0014\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u000e0\u0017H\'J\u001c\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00050\u000e2\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u001c\u00a8\u0006\u001d"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/db/dao/BookmarkItemDao;", "", "delete", "", "item", "Lcom/flamyoad/tsukiviewer/core/model/BookmarkItem;", "(Lcom/flamyoad/tsukiviewer/core/model/BookmarkItem;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "", "absolutePath", "Ljava/io/File;", "groupName", "", "(Ljava/io/File;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "items", "", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteFromAllGroups", "path", "(Ljava/io/File;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "exists", "", "folderPath", "from", "Landroidx/lifecycle/LiveData;", "insert", "", "selectAll", "selectFrom", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "core_debug"})
@androidx.room.Dao()
@androidx.room.TypeConverters(value = {com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter.class})
public abstract interface BookmarkItemDao {
    
    @androidx.room.Query(value = "SELECT * FROM bookmark_item")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkItem>> selectAll();
    
    @androidx.room.Query(value = "SELECT * FROM bookmark_item WHERE parentName = :groupName")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkItem>> from(@org.jetbrains.annotations.NotNull()
    java.lang.String groupName);
    
    @androidx.room.Query(value = "SELECT * FROM bookmark_item WHERE parentName = :groupName")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object selectFrom(@org.jetbrains.annotations.NotNull()
    java.lang.String groupName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkItem>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT EXISTS(SELECT * FROM bookmark_item \n                      WHERE parentName = :groupName AND absolutePath = :folderPath)\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object exists(@org.jetbrains.annotations.NotNull()
    java.io.File folderPath, @org.jetbrains.annotations.NotNull()
    java.lang.String groupName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @androidx.room.Insert(onConflict = 5)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.BookmarkItem item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Insert(onConflict = 5)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkItem> items, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.Long>> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.BookmarkItem item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkItem> items, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "DELETE FROM bookmark_item WHERE absolutePath = :absolutePath AND parentName = :groupName")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    java.io.File absolutePath, @org.jetbrains.annotations.NotNull()
    java.lang.String groupName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Query(value = "DELETE FROM bookmark_item WHERE absolutePath = :path")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteFromAllGroups(@org.jetbrains.annotations.NotNull()
    java.io.File path, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}