package com.flamyoad.tsukiviewer.core.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u0000 >2\u00020\u0001:\u0001>B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u001e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010\u0012J\u0012\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00160\u00150\u0014J\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015H\u0086@\u00a2\u0006\u0002\u0010\u0018J\u001c\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00160\u00152\u0006\u0010\u001a\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010\u001cJ\u0012\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001e0\u00150\u0014J\u001a\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001e0\u00150\u00142\u0006\u0010\u001f\u001a\u00020\u0016J\u001c\u0010 \u001a\b\u0012\u0004\u0012\u00020\u001e0\u00152\u0006\u0010\u001f\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010!J\u001c\u0010 \u001a\b\u0012\u0004\u0012\u00020\u001e0\u00152\u0006\u0010\"\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010#J\u0014\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00160\u00142\u0006\u0010%\u001a\u00020\u0010J\u0010\u0010&\u001a\u00020\u00102\u0006\u0010\'\u001a\u00020(H\u0002J\u0014\u0010)\u001a\b\u0012\u0004\u0012\u00020*0\u00142\u0006\u0010%\u001a\u00020\u0010J*\u0010+\u001a\u00020\u00102\f\u0010,\u001a\b\u0012\u0004\u0012\u00020-0\u00152\f\u0010.\u001a\b\u0012\u0004\u0012\u00020\u00100\u0015H\u0086@\u00a2\u0006\u0002\u0010/J\u0016\u00100\u001a\u00020\u000e2\u0006\u00101\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010!J\u0016\u00102\u001a\u00020\u000e2\u0006\u00103\u001a\u00020\u001eH\u0086@\u00a2\u0006\u0002\u00104J$\u00105\u001a\u00020(2\u0006\u0010\u001f\u001a\u00020\u00162\f\u00106\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0015H\u0086@\u00a2\u0006\u0002\u00107J\u0016\u00108\u001a\u00020\u000e2\u0006\u0010\u001f\u001a\u00020\u0016H\u0086@\u00a2\u0006\u0002\u0010!J\u0016\u00108\u001a\u00020\u000e2\u0006\u0010%\u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010#J:\u00109\u001a\u00020\u00102\u0006\u0010\u001a\u001a\u00020\u001b2\"\u0010:\u001a\u001e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020*0;j\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020*`<H\u0086@\u00a2\u0006\u0002\u0010=R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006?"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/repository/BookmarkRepository;", "", "db", "Lcom/flamyoad/tsukiviewer/core/db/AppDatabase;", "groupDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/BookmarkGroupDao;", "itemDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/BookmarkItemDao;", "(Lcom/flamyoad/tsukiviewer/core/db/AppDatabase;Lcom/flamyoad/tsukiviewer/core/db/dao/BookmarkGroupDao;Lcom/flamyoad/tsukiviewer/core/db/dao/BookmarkItemDao;)V", "getGroupDao", "()Lcom/flamyoad/tsukiviewer/core/db/dao/BookmarkGroupDao;", "getItemDao", "()Lcom/flamyoad/tsukiviewer/core/db/dao/BookmarkItemDao;", "changeGroupName", "", "oldName", "", "newName", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllGroups", "Landroidx/lifecycle/LiveData;", "", "Lcom/flamyoad/tsukiviewer/core/model/BookmarkGroup;", "getAllGroupsBlocking", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllGroupsFrom", "absolutePath", "Ljava/io/File;", "(Ljava/io/File;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllItems", "Lcom/flamyoad/tsukiviewer/core/model/BookmarkItem;", "group", "getAllItemsFrom", "(Lcom/flamyoad/tsukiviewer/core/model/BookmarkGroup;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "groupName", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getGroup", "name", "getNoun", "number", "", "groupNameExists", "", "insertAllItems", "doujinList", "Lcom/flamyoad/tsukiviewer/core/model/Doujin;", "groupNames", "(Ljava/util/List;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertGroup", "collection", "insertItem", "item", "(Lcom/flamyoad/tsukiviewer/core/model/BookmarkItem;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "moveItemsTo", "itemsToBeMoved", "(Lcom/flamyoad/tsukiviewer/core/model/BookmarkGroup;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeGroup", "wipeAndInsertNew", "hashMap", "Ljava/util/HashMap;", "Lkotlin/collections/HashMap;", "(Ljava/io/File;Ljava/util/HashMap;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "core_debug"})
public final class BookmarkRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.AppDatabase db = null;
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.dao.BookmarkGroupDao groupDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.dao.BookmarkItemDao itemDao = null;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String DEFAULT_BOOKMARK_GROUP = "Default Bookmark Group";
    @org.jetbrains.annotations.NotNull()
    public static final com.flamyoad.tsukiviewer.core.repository.BookmarkRepository.Companion Companion = null;
    
    @javax.inject.Inject()
    public BookmarkRepository(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase db, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.dao.BookmarkGroupDao groupDao, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.dao.BookmarkItemDao itemDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.BookmarkGroupDao getGroupDao() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.BookmarkItemDao getItemDao() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkItem>> getAllItems() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkItem>> getAllItems(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.BookmarkGroup group) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllItemsFrom(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.BookmarkGroup group, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkItem>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllItemsFrom(@org.jetbrains.annotations.NotNull()
    java.lang.String groupName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkItem>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<com.flamyoad.tsukiviewer.core.model.BookmarkGroup> getGroup(@org.jetbrains.annotations.NotNull()
    java.lang.String name) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkGroup>> getAllGroups() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Boolean> groupNameExists(@org.jetbrains.annotations.NotNull()
    java.lang.String name) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllGroupsFrom(@org.jetbrains.annotations.NotNull()
    java.io.File absolutePath, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkGroup>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object changeGroupName(@org.jetbrains.annotations.NotNull()
    java.lang.String oldName, @org.jetbrains.annotations.NotNull()
    java.lang.String newName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object removeGroup(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.BookmarkGroup group, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object removeGroup(@org.jetbrains.annotations.NotNull()
    java.lang.String name, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllGroupsBlocking(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkGroup>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertGroup(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.BookmarkGroup collection, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertItem(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.BookmarkItem item, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object moveItemsTo(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.BookmarkGroup group, @org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.BookmarkItem> itemsToBeMoved, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object wipeAndInsertNew(@org.jetbrains.annotations.NotNull()
    java.io.File absolutePath, @org.jetbrains.annotations.NotNull()
    java.util.HashMap<java.lang.String, java.lang.Boolean> hashMap, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertAllItems(@org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.Doujin> doujinList, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> groupNames, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    private final java.lang.String getNoun(int number) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/repository/BookmarkRepository$Companion;", "", "()V", "DEFAULT_BOOKMARK_GROUP", "", "core_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}