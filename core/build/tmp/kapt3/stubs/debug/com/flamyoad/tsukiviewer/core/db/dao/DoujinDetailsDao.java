package com.flamyoad.tsukiviewer.core.db.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u001c\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\u00112\u0006\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\"\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u00112\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\f0\u0011H\u00a7@\u00a2\u0006\u0002\u0010\u0014J*\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u00112\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\f0\u00112\u0006\u0010\u0015\u001a\u00020\u0016H\u00a7@\u00a2\u0006\u0002\u0010\u0017J\u001c\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00050\u00112\u0006\u0010\u0019\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\f0\u00112\u0006\u0010\u000b\u001a\u00020\fH\'J\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\u0011H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00050\u0011H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001f0\u0011H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010 \u001a\b\u0012\u0004\u0012\u00020\u001c0!2\u0006\u0010\"\u001a\u00020\fH\'J\u0016\u0010#\u001a\b\u0012\u0004\u0012\u00020\u001c0!2\u0006\u0010\u000b\u001a\u00020\fH\'J\u0018\u0010$\u001a\u0004\u0018\u00010\u001c2\u0006\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010%\u001a\u00020&2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\'\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006("}, d2 = {"Lcom/flamyoad/tsukiviewer/core/db/dao/DoujinDetailsDao;", "", "delete", "", "doujinDetails", "Lcom/flamyoad/tsukiviewer/core/model/DoujinDetails;", "(Lcom/flamyoad/tsukiviewer/core/model/DoujinDetails;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "existsByAbsolutePath", "", "absolutePath", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "existsByTitle", "fullTitleEnglish", "findByAbsolutePath", "", "findByTags", "tags", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "tagCount", "", "(Ljava/util/List;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "findByTitle", "query", "findShortTitleByPath", "getAllLongDetails", "Lcom/flamyoad/tsukiviewer/core/model/DoujinDetailsWithTags;", "getAllShortDetails", "getAllShortTitles", "Lcom/flamyoad/tsukiviewer/core/model/ShortTitle;", "getLongDetailsByFullTitle", "Landroidx/lifecycle/LiveData;", "fullTitle", "getLongDetailsByPath", "getLongDetailsByPathBlocking", "insert", "", "update", "core_debug"})
@androidx.room.Dao()
@androidx.room.TypeConverters(value = {com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter.class})
public abstract interface DoujinDetailsDao {
    
    @androidx.room.Insert(onConflict = 5)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.DoujinDetails doujinDetails, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.DoujinDetails doujinDetails, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.DoujinDetails doujinDetails, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM doujin_details")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT EXISTS(SELECT * FROM doujin_details WHERE fullTitleEnglish = :fullTitleEnglish)")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object existsByTitle(@org.jetbrains.annotations.NotNull()
    java.lang.String fullTitleEnglish, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @androidx.room.Query(value = "SELECT EXISTS(SELECT * FROM doujin_details WHERE absolutePath = :absolutePath)")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object existsByAbsolutePath(@org.jetbrains.annotations.NotNull()
    java.lang.String absolutePath, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @androidx.room.Query(value = "SELECT shortTitleEnglish, absolutePath FROM doujin_details")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllShortTitles(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.ShortTitle>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT * FROM doujin_details \n        WHERE fullTitleEnglish LIKE \'%\' || :query || \'%\' OR \n        fullTitleJapanese LIKE \'%\' || :query || \'%\'\n        ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object findByTitle(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM doujin_details WHERE absolutePath = :absolutePath")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object findByAbsolutePath(@org.jetbrains.annotations.NotNull()
    java.lang.String absolutePath, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion);
    
    @androidx.room.Query(value = "SELECT shortTitleEnglish FROM doujin_details WHERE absolutePath = :absolutePath")
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.List<java.lang.String> findShortTitleByPath(@org.jetbrains.annotations.NotNull()
    java.lang.String absolutePath);
    
    @androidx.room.Query(value = "\n        SELECT * FROM doujin_tags as dt\n        INNER JOIN doujin_details ON doujin_details.id = dt.doujinId\n        INNER JOIN tags ON tags.tagId = dt.tagId\n        WHERE name IN (:tags)\n        GROUP BY doujinId\n        HAVING COUNT(doujinId) = :tagCount\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object findByTags(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> tags, int tagCount, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion);
    
    @androidx.room.Query(value = "\n        SELECT * FROM doujin_tags as dt\n        INNER JOIN doujin_details ON doujin_details.id = dt.doujinId\n        INNER JOIN tags ON tags.tagId = dt.tagId\n        WHERE name IN (:tags)\n        GROUP BY doujinId\n        HAVING COUNT(doujinId) = 1\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object findByTags(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> tags, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM doujin_details")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllShortDetails(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetails>> $completion);
    
    @androidx.room.Transaction()
    @androidx.room.Query(value = "SELECT * FROM doujin_details")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllLongDetails(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.DoujinDetailsWithTags>> $completion);
    
    @androidx.room.Transaction()
    @androidx.room.Query(value = "SELECT * FROM doujin_details WHERE fullTitleEnglish = :fullTitle")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<com.flamyoad.tsukiviewer.core.model.DoujinDetailsWithTags> getLongDetailsByFullTitle(@org.jetbrains.annotations.NotNull()
    java.lang.String fullTitle);
    
    @androidx.room.Transaction()
    @androidx.room.Query(value = "SELECT * FROM doujin_details WHERE absolutePath = :absolutePath")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<com.flamyoad.tsukiviewer.core.model.DoujinDetailsWithTags> getLongDetailsByPath(@org.jetbrains.annotations.NotNull()
    java.lang.String absolutePath);
    
    @androidx.room.Transaction()
    @androidx.room.Query(value = "SELECT * FROM doujin_details WHERE absolutePath = :absolutePath")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLongDetailsByPathBlocking(@org.jetbrains.annotations.NotNull()
    java.lang.String absolutePath, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.flamyoad.tsukiviewer.core.model.DoujinDetailsWithTags> $completion);
}