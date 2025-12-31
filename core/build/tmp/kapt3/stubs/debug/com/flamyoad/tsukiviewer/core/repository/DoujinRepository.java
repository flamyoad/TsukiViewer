package com.flamyoad.tsukiviewer.core.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ$\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u0018J&\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u0018H\u0002J\u0016\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J\u0016\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J\u001a\u0010\u001c\u001a\u00020\u001d*\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\u001e\u001a\u00020\u001fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\"\u0010\t\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006 "}, d2 = {"Lcom/flamyoad/tsukiviewer/core/repository/DoujinRepository;", "", "application", "Landroid/app/Application;", "doujinDetailsDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/DoujinDetailsDao;", "pathDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/IncludedPathDao;", "(Landroid/app/Application;Lcom/flamyoad/tsukiviewer/core/db/dao/DoujinDetailsDao;Lcom/flamyoad/tsukiviewer/core/db/dao/IncludedPathDao;)V", "cachedDoujinList", "", "Lcom/flamyoad/tsukiviewer/core/model/Doujin;", "getCachedDoujinList", "()Ljava/util/List;", "setCachedDoujinList", "(Ljava/util/List;)V", "contentResolver", "Landroid/content/ContentResolver;", "scanForDoujins", "Lkotlinx/coroutines/flow/Flow;", "keyword", "", "tags", "shouldIncludeAllTags", "", "searchFromDatabase", "searchFromExistingList", "searchFromFileExplorer", "addIfNotNull", "", "doujinDetails", "Lcom/flamyoad/tsukiviewer/core/model/DoujinDetails;", "core_debug"})
public final class DoujinRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.app.Application application = null;
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.dao.DoujinDetailsDao doujinDetailsDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao pathDao = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.ContentResolver contentResolver = null;
    @org.jetbrains.annotations.Nullable()
    private java.util.List<com.flamyoad.tsukiviewer.core.model.Doujin> cachedDoujinList;
    
    @javax.inject.Inject()
    public DoujinRepository(@org.jetbrains.annotations.NotNull()
    android.app.Application application, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.dao.DoujinDetailsDao doujinDetailsDao, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao pathDao) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.flamyoad.tsukiviewer.core.model.Doujin> getCachedDoujinList() {
        return null;
    }
    
    public final void setCachedDoujinList(@org.jetbrains.annotations.Nullable()
    java.util.List<com.flamyoad.tsukiviewer.core.model.Doujin> p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.flamyoad.tsukiviewer.core.model.Doujin> scanForDoujins(@org.jetbrains.annotations.NotNull()
    java.lang.String keyword, @org.jetbrains.annotations.NotNull()
    java.lang.String tags, boolean shouldIncludeAllTags) {
        return null;
    }
    
    private final kotlinx.coroutines.flow.Flow<com.flamyoad.tsukiviewer.core.model.Doujin> searchFromDatabase(java.lang.String keyword, java.lang.String tags, boolean shouldIncludeAllTags) {
        return null;
    }
    
    private final kotlinx.coroutines.flow.Flow<com.flamyoad.tsukiviewer.core.model.Doujin> searchFromFileExplorer(java.lang.String keyword) {
        return null;
    }
    
    private final kotlinx.coroutines.flow.Flow<com.flamyoad.tsukiviewer.core.model.Doujin> searchFromExistingList(java.lang.String keyword) {
        return null;
    }
    
    private final void addIfNotNull(java.util.List<com.flamyoad.tsukiviewer.core.model.Doujin> $this$addIfNotNull, com.flamyoad.tsukiviewer.core.model.DoujinDetails doujinDetails) {
    }
}