package com.flamyoad.tsukiviewer.core.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u0016\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\u0006\u0010\u0011\u001a\u00020\u0005H\'J\u0016\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\n0\u000f2\u0006\u0010\u0011\u001a\u00020\u0005H\'J\u0014\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00140\u000fH\'J\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\n0\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u001c\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00050\u00142\u0006\u0010\u0018\u001a\u00020\u0019H\u00a7@\u00a2\u0006\u0002\u0010\u001aJ\u0016\u0010\u001b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\u001c\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000b\u00a8\u0006\u001d"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/db/dao/BookmarkGroupDao;", "", "changeName", "", "oldName", "", "newName", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "delete", "collection", "Lcom/flamyoad/tsukiviewer/core/model/BookmarkGroup;", "(Lcom/flamyoad/tsukiviewer/core/model/BookmarkGroup;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "collectionName", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "exists", "Landroidx/lifecycle/LiveData;", "", "name", "get", "getAll", "", "getAllBlocking", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCollectionNamesFrom", "absolutePath", "Ljava/io/File;", "(Ljava/io/File;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "update", "core_debug"})
@androidx.room.Dao()
@androidx.room.TypeConverters(value = {com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter.class})
public abstract interface BookmarkGroupDao {
    
    @androidx.room.Insert(onConflict = 5)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.BookmarkGroup collection, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.BookmarkGroup collection, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.BookmarkGroup collection, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM bookmark_group WHERE name = :collectionName")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    java.lang.String collectionName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "\n        UPDATE bookmark_group \n        SET name = :newName \n        WHERE name = :oldName\n        ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object changeName(@org.jetbrains.annotations.NotNull()
    java.lang.String oldName, @org.jetbrains.annotations.NotNull()
    java.lang.String newName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM bookmark_group WHERE name = :name")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<com.flamyoad.tsukiviewer.core.model.BookmarkGroup> get(@org.jetbrains.annotations.NotNull()
    java.lang.String name);
    
    @androidx.room.Query(value = "SELECT * FROM bookmark_group")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkGroup>> getAll();
    
    @androidx.room.Query(value = "SELECT EXISTS(SELECT * FROM bookmark_group WHERE name = :name)")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.lang.Boolean> exists(@org.jetbrains.annotations.NotNull()
    java.lang.String name);
    
    @androidx.room.Query(value = "SELECT * FROM bookmark_group")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllBlocking(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkGroup>> $completion);
    
    @androidx.room.Query(value = "SELECT parentName FROM bookmark_item WHERE absolutePath = :absolutePath")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCollectionNamesFrom(@org.jetbrains.annotations.NotNull()
    java.io.File absolutePath, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion);
}