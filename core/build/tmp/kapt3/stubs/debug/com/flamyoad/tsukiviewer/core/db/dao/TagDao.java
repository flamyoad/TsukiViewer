package com.flamyoad.tsukiviewer.core.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\bg\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u0016\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u0016\u0010\b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u000e\u0010\u000f\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0010J\u001e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0007J \u0010\u0013\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u0014\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00160\u0015H\'J\u001c\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00160\u00152\u0006\u0010\u0018\u001a\u00020\u0005H\'J$\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00160\u00152\u0006\u0010\u0018\u001a\u00020\u00052\u0006\u0010\u0019\u001a\u00020\u001aH\'J\u001c\u0010\u001b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00160\u00152\u0006\u0010\u001c\u001a\u00020\u0005H\'J,\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00160\u00152\u0006\u0010\u001c\u001a\u00020\u00052\u0006\u0010\u0018\u001a\u00020\u00052\u0006\u0010\u0019\u001a\u00020\u001aH\'J\u001e\u0010\u001e\u001a\u00020\r2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u001e\u0010\u001f\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u0016\u0010 \u001a\u00020\r2\u0006\u0010\t\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u000b\u00a8\u0006!"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/db/dao/TagDao;", "", "decrementCount", "", "type", "", "name", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "delete", "tag", "Lcom/flamyoad/tsukiviewer/core/model/Tag;", "(Lcom/flamyoad/tsukiviewer/core/model/Tag;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "tagId", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "exists", "", "get", "getAll", "Landroidx/lifecycle/LiveData;", "", "getAllWithFilter", "keyword", "sortMode", "Lcom/flamyoad/tsukiviewer/core/model/TagSortingMode;", "getByCategory", "category", "getByCategoryWithFilter", "getId", "incrementCount", "insert", "core_debug"})
@androidx.room.Dao()
@androidx.room.TypeConverters(value = {com.flamyoad.tsukiviewer.core.db.typeconverter.TagSortingModeConverter.class})
public abstract interface TagDao {
    
    @androidx.room.Insert()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.Tag tag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.Tag tag, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM tags WHERE tagId = :tagId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(long tagId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM tags")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM tags ORDER BY name")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.Tag>> getAll();
    
    @androidx.room.Query(value = "\n        SELECT * FROM tags \n        WHERE name LIKE \'%\' || :keyword || \'%\'\n        ORDER BY name \n        ")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.Tag>> getAllWithFilter(@org.jetbrains.annotations.NotNull()
    java.lang.String keyword);
    
    @androidx.room.Query(value = "\n        SELECT * FROM tags \n        WHERE name LIKE \'%\' || :keyword || \'%\'\n        ORDER BY \n        CASE WHEN :sortMode = \'NAME_ASCENDING\' THEN name END,\n        CASE WHEN :sortMode = \'NAME_DESCENDING\' THEN name END DESC,\n        CASE WHEN :sortMode = \'COUNT_ASCENDING\' THEN count END,\n        CASE WHEN :sortMode = \'COUNT_DESCENDING\' THEN count END DESC\n        ")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.Tag>> getAllWithFilter(@org.jetbrains.annotations.NotNull()
    java.lang.String keyword, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.TagSortingMode sortMode);
    
    @androidx.room.Query(value = "SELECT * FROM tags WHERE type = :category ORDER BY name")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.Tag>> getByCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String category);
    
    @androidx.room.Query(value = "\n        SELECT * FROM tags \n        WHERE type = :category AND name LIKE \'%\' || :keyword || \'%\'\n        ORDER BY \n        CASE WHEN :sortMode = \'NAME_ASCENDING\' THEN name END,\n        CASE WHEN :sortMode = \'NAME_DESCENDING\' THEN name END DESC,\n        CASE WHEN :sortMode = \'COUNT_ASCENDING\' THEN count END,\n        CASE WHEN :sortMode = \'COUNT_DESCENDING\' THEN count END DESC\n        ")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.Tag>> getByCategoryWithFilter(@org.jetbrains.annotations.NotNull()
    java.lang.String category, @org.jetbrains.annotations.NotNull()
    java.lang.String keyword, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.TagSortingMode sortMode);
    
    @androidx.room.Query(value = "SELECT * FROM tags WHERE type = :type AND name = :name")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object get(@org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.flamyoad.tsukiviewer.core.model.Tag> $completion);
    
    @androidx.room.Query(value = "SELECT tagId from tags WHERE type = :type AND name = :name")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getId(@org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Query(value = "SELECT EXISTS(SELECT * FROM tags WHERE type = :type AND name = :name)")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object exists(@org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @androidx.room.Query(value = "UPDATE tags SET count = count + 1 WHERE type = :type AND name = :name")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object incrementCount(@org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE tags SET count = count - 1 WHERE type = :type AND name = :name")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object decrementCount(@org.jetbrains.annotations.NotNull()
    java.lang.String type, @org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}