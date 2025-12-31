package com.flamyoad.tsukiviewer.core.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\'J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\'J\u0016\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u0014\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00130\u0012H\'J\u0018\u0010\u0014\u001a\u0004\u0018\u00010\u00052\u0006\u0010\f\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u0010\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0004\u001a\u00020\u0005H\'\u00a8\u0006\u0017"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/db/dao/RecentTabDao;", "", "delete", "", "tab", "Lcom/flamyoad/tsukiviewer/core/model/RecentTab;", "deleteAllExcept", "", "tabId", "", "existsByPath", "", "path", "", "get", "id", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "Landroidx/lifecycle/LiveData;", "", "getByPath", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "core_debug"})
@androidx.room.Dao()
@androidx.room.TypeConverters(value = {com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter.class})
public abstract interface RecentTabDao {
    
    @androidx.room.Query(value = "SELECT * FROM recent_tabs")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.RecentTab>> getAll();
    
    @androidx.room.Query(value = "SELECT * FROM recent_tabs WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object get(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.flamyoad.tsukiviewer.core.model.RecentTab> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM recent_tabs WHERE dirPath = :path")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getByPath(@org.jetbrains.annotations.NotNull()
    java.lang.String path, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.flamyoad.tsukiviewer.core.model.RecentTab> $completion);
    
    @androidx.room.Query(value = "SELECT EXISTS (SELECT * FROM recent_tabs WHERE dirPath = :path)")
    public abstract boolean existsByPath(@org.jetbrains.annotations.NotNull()
    java.lang.String path);
    
    @androidx.room.Insert()
    public abstract long insert(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.RecentTab tab);
    
    @androidx.room.Delete()
    public abstract void delete(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.RecentTab tab);
    
    @androidx.room.Query(value = "DELETE FROM recent_tabs WHERE id != :tabId")
    public abstract int deleteAllExcept(long tabId);
}