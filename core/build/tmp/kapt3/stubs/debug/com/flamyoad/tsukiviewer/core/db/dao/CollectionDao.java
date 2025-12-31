package com.flamyoad.tsukiviewer.core.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\n\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\f2\u0006\u0010\r\u001a\u00020\bH\'J\u0014\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u000f0\fH\'J\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\u000fH\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u0014\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u000f0\fH\'J\u001c\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u000f0\f2\u0006\u0010\u0014\u001a\u00020\u0015H\'J\u0016\u0010\u0016\u001a\u00020\u00052\u0006\u0010\r\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\u0017\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001e\u0010\u0018\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\b2\u0006\u0010\u0019\u001a\u00020\u001aH\u00a7@\u00a2\u0006\u0002\u0010\u001b\u00a8\u0006\u001c"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionDao;", "", "delete", "", "collection", "Lcom/flamyoad/tsukiviewer/core/model/Collection;", "(Lcom/flamyoad/tsukiviewer/core/model/Collection;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "id", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteThumbnail", "get", "Landroidx/lifecycle/LiveData;", "collectionId", "getAll", "", "getAllBlocking", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllWithCriterias", "Lcom/flamyoad/tsukiviewer/core/model/CollectionWithCriterias;", "keyword", "", "getBlocking", "insert", "updateThumbnail", "thumbnail", "Ljava/io/File;", "(JLjava/io/File;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "core_debug"})
@androidx.room.Dao()
@androidx.room.TypeConverters(value = {com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter.class})
public abstract interface CollectionDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.Collection collection, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.Collection collection, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM collection WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM collection")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.Collection>> getAll();
    
    @androidx.room.Query(value = "SELECT * FROM collection")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllBlocking(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.Collection>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM collection")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.CollectionWithCriterias>> getAllWithCriterias();
    
    @androidx.room.Query(value = "\n        SELECT * FROM collection \n        WHERE name LIKE \'%\' || :keyword || \'%\'\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.CollectionWithCriterias>> getAllWithCriterias(@org.jetbrains.annotations.NotNull()
    java.lang.String keyword);
    
    @androidx.room.Query(value = "SELECT * FROM collection WHERE id = :collectionId")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<com.flamyoad.tsukiviewer.core.model.Collection> get(long collectionId);
    
    @androidx.room.Query(value = "SELECT * FROM collection WHERE id = :collectionId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getBlocking(long collectionId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.flamyoad.tsukiviewer.core.model.Collection> $completion);
    
    @androidx.room.Query(value = "\n        UPDATE collection \n        SET coverPhoto = :thumbnail \n        WHERE id = :collectionId\n        ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateThumbnail(long collectionId, @org.jetbrains.annotations.NotNull()
    java.io.File thumbnail, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE collection SET coverPhoto = \'\' WHERE id = :id ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteThumbnail(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}