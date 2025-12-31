package com.flamyoad.tsukiviewer.core.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u000f\bg\u0018\u00002\u00020\u0001J*\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\"\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u000bJ*\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\u0006\u0010\u000e\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ@\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\u0006\u0010\u000e\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\u0010J8\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\u0006\u0010\u000e\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\u0012J\"\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u000bJ8\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\u0006\u0010\u0007\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\u0012J0\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0016\u00a8\u0006\u0017"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/db/dao/CollectionDoujinDao;", "", "searchExcludedAnd", "", "Lcom/flamyoad/tsukiviewer/core/model/DoujinDetails;", "excludedTagsId", "", "excludedTagsCount", "", "(Ljava/util/List;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchExcludedOr", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchIncludedAnd", "includedTagsId", "includedTagsCount", "searchIncludedAndExcludedAnd", "(Ljava/util/List;Ljava/util/List;IILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchIncludedAndExcludedOr", "(Ljava/util/List;Ljava/util/List;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchIncludedOr", "searchIncludedOrExcludedAnd", "searchIncludedOrExcludedOr", "(Ljava/util/List;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "core_debug"})
@androidx.room.Dao()
public abstract interface CollectionDoujinDao {
    
    @androidx.room.Query(value = "\n        SELECT * FROM doujin_details as details\n        INNER JOIN doujin_tags ON details.id = doujin_tags.doujinId\n        WHERE tagId IN (:includedTagsId) \n        GROUP BY details.id\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchIncludedOr(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> includedTagsId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT * FROM doujin_tags\n        INNER JOIN doujin_details ON doujin_tags.doujinId = doujin_details.id\n        WHERE doujin_tags.tagId IN (:includedTagsId)  ----> Replace with list args\n        GROUP BY doujin_tags.doujinId\n        HAVING COUNT(*) = :includedTagsCount\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchIncludedAnd(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> includedTagsId, int includedTagsCount, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT * FROM doujin_details WHERE id IN (\n\t        SELECT doujinId FROM doujin_tags\n\t\t        EXCEPT\n\t        SELECT doujinId FROM doujin_tags\n\t        WHERE doujin_tags.tagId IN (:excludedTagsId)\n\t        GROUP BY doujin_tags.doujinId\n        ) \n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchExcludedOr(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> excludedTagsId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT * FROM doujin_details WHERE id IN (\n\t        SELECT doujinId FROM doujin_tags\n\t\t        EXCEPT\n\t        SELECT doujinId FROM doujin_tags\n\t        INNER JOIN doujin_details ON doujin_tags.doujinId = doujin_details.id\n\t        WHERE doujin_tags.tagId IN (:excludedTagsId) \n\t        GROUP BY doujin_tags.doujinId\n\t        HAVING COUNT(*) = :excludedTagsCount\n) \n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchExcludedAnd(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> excludedTagsId, int excludedTagsCount, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT * FROM doujin_details \n        WHERE id IN (\n\t        SELECT doujinId FROM doujin_tags\n\t        WHERE doujin_tags.tagId IN (:includedTagsId) \n\t        GROUP BY doujinId\n\t\t        EXCEPT \n\t        SELECT doujinId FROM doujin_tags\n            WHERE doujin_tags.tagId IN (:excludedTagsId)\n\t        GROUP BY doujin_tags.doujinId)\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchIncludedOrExcludedOr(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> includedTagsId, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> excludedTagsId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT * FROM doujin_details \n        WHERE id IN (\n\t        SELECT doujinId FROM doujin_tags\n\t        WHERE doujin_tags.tagId IN (:includedTagsId) \n\t        GROUP BY doujinId\n\t\t        EXCEPT \n\t        SELECT doujinId FROM doujin_tags\n\t        WHERE doujin_tags.tagId IN (:excludedTagsId)\n\t        GROUP BY doujinId\n\t        HAVING COUNT(*) = :excludedTagsCount)  -----> Replace with list size\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchIncludedOrExcludedAnd(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> includedTagsId, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> excludedTagsId, int excludedTagsCount, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT * FROM doujin_details \n        WHERE id IN (\n\t        SELECT doujinId FROM doujin_tags\n\t        WHERE doujin_tags.tagId IN (:includedTagsId) \n\t        GROUP BY doujinId\n\t        HAVING COUNT(*) = :includedTagsCount\n\t\t        EXCEPT \n\t        SELECT doujinId FROM doujin_tags\n\t        WHERE doujin_tags.tagId IN (:excludedTagsId)\n\t        GROUP BY doujinId)\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchIncludedAndExcludedOr(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> includedTagsId, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> excludedTagsId, int includedTagsCount, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT * FROM doujin_details \n        WHERE id IN (\n\t        SELECT doujinId FROM doujin_tags\n\t        WHERE doujin_tags.tagId IN (:includedTagsId)  \n\t        GROUP BY doujinId\n\t        HAVING COUNT(*) = :includedTagsCount   \n\t\t        EXCEPT \n\t        SELECT doujinId FROM doujin_tags\n\t        WHERE doujin_tags.tagId IN (:excludedTagsId)\n\t        GROUP BY doujinId\n\t        HAVING COUNT(*) = :excludedTagsCount  \n)\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchIncludedAndExcludedAnd(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> includedTagsId, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.Long> excludedTagsId, int includedTagsCount, int excludedTagsCount, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion);
}