package com.flamyoad.tsukiviewer.core.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000f\u001a\u00020\u0006H\u00c6\u0003J\'\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001J\t\u0010\u0016\u001a\u00020\u0006H\u00d6\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t\u00a8\u0006\u0017"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/model/IncludedFolder;", "", "dir", "Ljava/io/File;", "parentDir", "lastName", "", "(Ljava/io/File;Ljava/io/File;Ljava/lang/String;)V", "getDir", "()Ljava/io/File;", "getLastName", "()Ljava/lang/String;", "getParentDir", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "core_debug"})
@androidx.room.Entity(tableName = "included_folders", foreignKeys = {@androidx.room.ForeignKey(entity = com.flamyoad.tsukiviewer.core.model.IncludedPath.class, parentColumns = {"dir"}, childColumns = {"parentDir"}, onDelete = 5, deferred = true)})
@androidx.room.TypeConverters(value = {com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter.class})
public final class IncludedFolder {
    @androidx.room.PrimaryKey()
    @org.jetbrains.annotations.NotNull()
    private final java.io.File dir = null;
    @org.jetbrains.annotations.NotNull()
    private final java.io.File parentDir = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String lastName = null;
    
    public IncludedFolder(@org.jetbrains.annotations.NotNull()
    java.io.File dir, @org.jetbrains.annotations.NotNull()
    java.io.File parentDir, @org.jetbrains.annotations.NotNull()
    java.lang.String lastName) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.io.File getDir() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.io.File getParentDir() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLastName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.io.File component1() {
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
    
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.model.IncludedFolder copy(@org.jetbrains.annotations.NotNull()
    java.io.File dir, @org.jetbrains.annotations.NotNull()
    java.io.File parentDir, @org.jetbrains.annotations.NotNull()
    java.lang.String lastName) {
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