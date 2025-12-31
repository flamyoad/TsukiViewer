package com.flamyoad.tsukiviewer.core.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u000f\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\'\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\tB5\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\u0002\u0010\fJ\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0014J\t\u0010\u001e\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0007H\u00c6\u0003J\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010!\u001a\u0004\u0018\u00010\u000bH\u00c6\u0003JD\u0010\"\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000bH\u00c6\u0001\u00a2\u0006\u0002\u0010#J\u0013\u0010$\u001a\u00020\u00172\b\u0010%\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010&\u001a\u00020\'H\u00d6\u0001J\t\u0010(\u001a\u00020\u0007H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0018\u0010\n\u001a\u0004\u0018\u00010\u000b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u0015\u001a\u0004\b\u0013\u0010\u0014R\u001e\u0010\u0016\u001a\u00020\u00178\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001c\u00a8\u0006)"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/model/BookmarkItem;", "", "id", "", "absolutePath", "Ljava/io/File;", "parentName", "", "dateAdded", "(JLjava/io/File;Ljava/lang/String;J)V", "doujin", "Lcom/flamyoad/tsukiviewer/core/model/Doujin;", "(Ljava/lang/Long;Ljava/io/File;Ljava/lang/String;JLcom/flamyoad/tsukiviewer/core/model/Doujin;)V", "getAbsolutePath", "()Ljava/io/File;", "getDateAdded", "()J", "getDoujin", "()Lcom/flamyoad/tsukiviewer/core/model/Doujin;", "getId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "isSelected", "", "()Z", "setSelected", "(Z)V", "getParentName", "()Ljava/lang/String;", "component1", "component2", "component3", "component4", "component5", "copy", "(Ljava/lang/Long;Ljava/io/File;Ljava/lang/String;JLcom/flamyoad/tsukiviewer/core/model/Doujin;)Lcom/flamyoad/tsukiviewer/core/model/BookmarkItem;", "equals", "other", "hashCode", "", "toString", "core_debug"})
@androidx.room.Entity(tableName = "bookmark_item", foreignKeys = {@androidx.room.ForeignKey(entity = com.flamyoad.tsukiviewer.core.model.BookmarkGroup.class, parentColumns = {"name"}, childColumns = {"parentName"}, onDelete = 5, onUpdate = 5)})
@androidx.room.TypeConverters(value = {com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter.class})
public final class BookmarkItem {
    @androidx.room.PrimaryKey(autoGenerate = true)
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long id = null;
    @org.jetbrains.annotations.NotNull()
    private final java.io.File absolutePath = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String parentName = null;
    private final long dateAdded = 0L;
    @androidx.room.Ignore()
    @org.jetbrains.annotations.Nullable()
    private final com.flamyoad.tsukiviewer.core.model.Doujin doujin = null;
    @androidx.room.Ignore()
    private boolean isSelected = false;
    
    public BookmarkItem(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, @org.jetbrains.annotations.NotNull()
    java.io.File absolutePath, @org.jetbrains.annotations.NotNull()
    java.lang.String parentName, long dateAdded, @org.jetbrains.annotations.Nullable()
    com.flamyoad.tsukiviewer.core.model.Doujin doujin) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.io.File getAbsolutePath() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getParentName() {
        return null;
    }
    
    public final long getDateAdded() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.flamyoad.tsukiviewer.core.model.Doujin getDoujin() {
        return null;
    }
    
    public BookmarkItem(long id, @org.jetbrains.annotations.NotNull()
    java.io.File absolutePath, @org.jetbrains.annotations.NotNull()
    java.lang.String parentName, long dateAdded) {
        super();
    }
    
    public final boolean isSelected() {
        return false;
    }
    
    public final void setSelected(boolean p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.io.File component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    public final long component4() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final com.flamyoad.tsukiviewer.core.model.Doujin component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.model.BookmarkItem copy(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, @org.jetbrains.annotations.NotNull()
    java.io.File absolutePath, @org.jetbrains.annotations.NotNull()
    java.lang.String parentName, long dateAdded, @org.jetbrains.annotations.Nullable()
    com.flamyoad.tsukiviewer.core.model.Doujin doujin) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}