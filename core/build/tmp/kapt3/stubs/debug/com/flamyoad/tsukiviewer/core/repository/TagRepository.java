package com.flamyoad.tsukiviewer.core.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0012\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006J\u001a\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u00062\u0006\u0010\n\u001a\u00020\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/repository/TagRepository;", "", "tagDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/TagDao;", "(Lcom/flamyoad/tsukiviewer/core/db/dao/TagDao;)V", "getAll", "Landroidx/lifecycle/LiveData;", "", "Lcom/flamyoad/tsukiviewer/core/model/Tag;", "getAllWithFilter", "keyword", "", "core_debug"})
public final class TagRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.dao.TagDao tagDao = null;
    
    @javax.inject.Inject()
    public TagRepository(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.dao.TagDao tagDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.Tag>> getAll() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.Tag>> getAllWithFilter(@org.jetbrains.annotations.NotNull()
    java.lang.String keyword) {
        return null;
    }
}