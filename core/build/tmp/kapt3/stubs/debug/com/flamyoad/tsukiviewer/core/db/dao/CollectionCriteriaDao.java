package com.flamyoad.tsukiviewer.core.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\b2\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u001c\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\t0\b2\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u001c\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\r0\t2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\t0\b2\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u001c\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\r0\t2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00120\t0\b2\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u001c\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\t2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0016H\u00a7@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0016H\u00a7@\u00a2\u0006\u0002\u0010\u0017\u00a8\u0006\u0019"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionCriteriaDao;", "", "delete", "", "collectionId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDirectories", "Landroidx/lifecycle/LiveData;", "", "Ljava/io/File;", "getDirectoriesBlocking", "getExcludedTags", "Lcom/flamyoad/tsukiviewer/core/model/Tag;", "getExcludedTagsBlocking", "getIncludedTags", "getIncludedTagsBlocking", "getTitles", "", "getTitlesBlocking", "insert", "criteria", "Lcom/flamyoad/tsukiviewer/core/model/CollectionCriteria;", "(Lcom/flamyoad/tsukiviewer/core/model/CollectionCriteria;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "update", "core_debug"})
@androidx.room.Dao()
@androidx.room.TypeConverters(value = {com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter.class})
public abstract interface CollectionCriteriaDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.CollectionCriteria criteria, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.CollectionCriteria criteria, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM collection_criteria WHERE collectionId = :collectionId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(long collectionId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "\n        SELECT value FROM collection_criteria\n        WHERE collectionId = :collectionId AND type = \'title\' \n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<java.lang.String>> getTitles(long collectionId);
    
    @androidx.room.Query(value = "\n        SELECT * FROM tags\n        WHERE tagId IN (SELECT value \n                       FROM COLLECTION_CRITERIA \n                       WHERE collectionId = :collectionId AND type = \'included_tags\') \n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.Tag>> getIncludedTags(long collectionId);
    
    @androidx.room.Query(value = "\n        SELECT * FROM tags\n        WHERE tagId IN (SELECT value \n                       FROM COLLECTION_CRITERIA \n                       WHERE collectionId = :collectionId AND type = \'excluded_tags\') \n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.Tag>> getExcludedTags(long collectionId);
    
    @androidx.room.Query(value = "\n        SELECT value FROM collection_criteria \n        WHERE collectionId = :collectionId AND type = \'directory\'\n    ")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<java.io.File>> getDirectories(long collectionId);
    
    @androidx.room.Query(value = "\n        SELECT value FROM collection_criteria\n        WHERE collectionId = :collectionId AND type = \'title\' \n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTitlesBlocking(long collectionId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT * FROM tags\n        WHERE tagId IN (SELECT value \n                       FROM COLLECTION_CRITERIA \n                       WHERE collectionId = :collectionId AND type = \'included_tags\') \n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getIncludedTagsBlocking(long collectionId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.Tag>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT * FROM tags\n        WHERE tagId IN (SELECT value \n                       FROM COLLECTION_CRITERIA \n                       WHERE collectionId = :collectionId AND type = \'excluded_tags\') \n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getExcludedTagsBlocking(long collectionId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.Tag>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT value FROM collection_criteria \n        WHERE collectionId = :collectionId AND type = \'directory\'\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getDirectoriesBlocking(long collectionId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<? extends java.io.File>> $completion);
}