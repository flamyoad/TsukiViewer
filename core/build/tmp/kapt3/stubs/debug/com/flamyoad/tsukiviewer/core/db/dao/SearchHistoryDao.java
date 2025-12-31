package com.flamyoad.tsukiviewer.core.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\'J\b\u0010\u0006\u001a\u00020\u0007H\'J\u0014\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00050\tH\'J\u0010\u0010\n\u001a\u0004\u0018\u00010\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u0010\u0010\f\u001a\u00020\u00072\u0006\u0010\u0004\u001a\u00020\u0005H\'\u00a8\u0006\r"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/db/dao/SearchHistoryDao;", "", "delete", "", "searchHistory", "Lcom/flamyoad/tsukiviewer/core/model/SearchHistory;", "deleteAll", "", "getAll", "Landroidx/paging/DataSource$Factory;", "getLatestItem", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insert", "core_debug"})
@androidx.room.Dao()
public abstract interface SearchHistoryDao {
    
    @androidx.room.Insert()
    public abstract void insert(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.SearchHistory searchHistory);
    
    @androidx.room.Delete()
    public abstract int delete(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.SearchHistory searchHistory);
    
    @androidx.room.Query(value = "SELECT * FROM search_history ORDER BY id DESC LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLatestItem(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.flamyoad.tsukiviewer.core.model.SearchHistory> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM search_history ORDER BY id DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.paging.DataSource.Factory<java.lang.Integer, com.flamyoad.tsukiviewer.core.model.SearchHistory> getAll();
    
    @androidx.room.Query(value = "DELETE FROM search_history")
    public abstract void deleteAll();
}