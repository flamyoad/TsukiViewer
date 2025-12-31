package com.flamyoad.tsukiviewer.core.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\b\u0007\u0018\u00002\u00020\u0001B\'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00110\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0015J\u0012\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00180\u00140\u0017J\u001a\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00180\u00140\u00172\u0006\u0010\u0019\u001a\u00020\u001aJ\u001c\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u00142\u0006\u0010\u0012\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u001c\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u00142\u0006\u0010\u0012\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u001c\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u001e0\u00142\u0006\u0010\u0012\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ\u001c\u0010 \u001a\b\u0012\u0004\u0012\u00020\u001a0\u00142\u0006\u0010\u0012\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u000fJ$\u0010!\u001a\u00020\f2\u0006\u0010\"\u001a\u00020\u00112\f\u0010#\u001a\b\u0012\u0004\u0012\u00020$0\u0014H\u0086@\u00a2\u0006\u0002\u0010%J0\u0010&\u001a\b\u0012\u0004\u0012\u00020\'0\u00142\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u001e0\u00142\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0014H\u0086@\u00a2\u0006\u0002\u0010*J0\u0010+\u001a\b\u0012\u0004\u0012\u00020\'0\u00142\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u001e0\u00142\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0014H\u0086@\u00a2\u0006\u0002\u0010*J0\u0010,\u001a\b\u0012\u0004\u0012\u00020\'0\u00142\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u001e0\u00142\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0014H\u0086@\u00a2\u0006\u0002\u0010*J0\u0010-\u001a\b\u0012\u0004\u0012\u00020\'0\u00142\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u001e0\u00142\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0014H\u0086@\u00a2\u0006\u0002\u0010*J\u001c\u0010.\u001a\u00020\f2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020$0\u0014H\u0086@\u00a2\u0006\u0002\u0010/J \u00100\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u00101\u001a\u00020\u001cH\u0086@\u00a2\u0006\u0002\u00102R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00063"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/repository/CollectionRepository;", "", "db", "Lcom/flamyoad/tsukiviewer/core/db/AppDatabase;", "collectionDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionDao;", "criteriaDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionCriteriaDao;", "collectionDoujinDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionDoujinDao;", "(Lcom/flamyoad/tsukiviewer/core/db/AppDatabase;Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionDao;Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionCriteriaDao;Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionDoujinDao;)V", "delete", "", "collectionId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "get", "Lcom/flamyoad/tsukiviewer/core/model/Collection;", "id", "getAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllWithCriterias", "Landroidx/lifecycle/LiveData;", "Lcom/flamyoad/tsukiviewer/core/model/CollectionWithCriterias;", "keyword", "", "getDirectories", "Ljava/io/File;", "getExcludedTags", "Lcom/flamyoad/tsukiviewer/core/model/Tag;", "getIncludedTags", "getTitles", "insert", "collection", "criterias", "Lcom/flamyoad/tsukiviewer/core/model/CollectionCriteria;", "(Lcom/flamyoad/tsukiviewer/core/model/Collection;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchIncludedAndExcludedAnd", "Lcom/flamyoad/tsukiviewer/core/model/DoujinDetails;", "includedTags", "excludedTags", "(Ljava/util/List;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchIncludedAndExcludedOr", "searchIncludedOrExcludedAnd", "searchIncludedOrExcludedOr", "update", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateThumbnail", "file", "(Ljava/lang/Long;Ljava/io/File;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "core_debug"})
public final class CollectionRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.AppDatabase db = null;
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.dao.CollectionDao collectionDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.dao.CollectionCriteriaDao criteriaDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.dao.CollectionDoujinDao collectionDoujinDao = null;
    
    @javax.inject.Inject()
    public CollectionRepository(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase db, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.dao.CollectionDao collectionDao, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.dao.CollectionCriteriaDao criteriaDao, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.dao.CollectionDoujinDao collectionDoujinDao) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object get(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.flamyoad.tsukiviewer.core.model.Collection> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.Collection>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.CollectionWithCriterias>> getAllWithCriterias() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.CollectionWithCriterias>> getAllWithCriterias(@org.jetbrains.annotations.NotNull()
    java.lang.String keyword) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.Collection collection, @org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.CollectionCriteria> criterias, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object delete(long collectionId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object update(@org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.CollectionCriteria> criterias, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateThumbnail(@org.jetbrains.annotations.Nullable()
    java.lang.Long collectionId, @org.jetbrains.annotations.NotNull()
    java.io.File file, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getTitles(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getIncludedTags(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.Tag>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getExcludedTags(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.Tag>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getDirectories(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<? extends java.io.File>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object searchIncludedOrExcludedOr(@org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.Tag> includedTags, @org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.Tag> excludedTags, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object searchIncludedOrExcludedAnd(@org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.Tag> includedTags, @org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.Tag> excludedTags, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object searchIncludedAndExcludedOr(@org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.Tag> includedTags, @org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.Tag> excludedTags, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object searchIncludedAndExcludedAnd(@org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.Tag> includedTags, @org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.Tag> excludedTags, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion) {
        return null;
    }
}