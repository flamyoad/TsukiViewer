package com.flamyoad.tsukiviewer.core.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0007\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\rJ\u001a\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u00100\u000f2\u0006\u0010\u0011\u001a\u00020\u0012J\u0016\u0010\u0013\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0014"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/repository/SearchHistoryRepository;", "", "searchHistoryDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/SearchHistoryDao;", "(Lcom/flamyoad/tsukiviewer/core/db/dao/SearchHistoryDao;)V", "getSearchHistoryDao", "()Lcom/flamyoad/tsukiviewer/core/db/dao/SearchHistoryDao;", "deleteAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteSingle", "item", "Lcom/flamyoad/tsukiviewer/core/model/SearchHistory;", "(Lcom/flamyoad/tsukiviewer/core/model/SearchHistory;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "Landroidx/lifecycle/LiveData;", "Landroidx/paging/PagedList;", "pageSize", "", "insertSearchHistory", "core_debug"})
public final class SearchHistoryRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.dao.SearchHistoryDao searchHistoryDao = null;
    
    @javax.inject.Inject()
    public SearchHistoryRepository(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.dao.SearchHistoryDao searchHistoryDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.SearchHistoryDao getSearchHistoryDao() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<androidx.paging.PagedList<com.flamyoad.tsukiviewer.core.model.SearchHistory>> getAll(int pageSize) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertSearchHistory(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.SearchHistory item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteSingle(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.SearchHistory item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}