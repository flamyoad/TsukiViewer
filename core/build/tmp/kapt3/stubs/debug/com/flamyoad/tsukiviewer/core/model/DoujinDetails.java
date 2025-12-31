package com.flamyoad.tsukiviewer.core.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0087\b\u0018\u0000 (2\u00020\u0001:\u0001(BA\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\rJ\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0015J\t\u0010\u001b\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u000bH\u00c6\u0003J\t\u0010 \u001a\u00020\u0007H\u00c6\u0003JV\u0010!\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00072\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u0007H\u00c6\u0001\u00a2\u0006\u0002\u0010\"J\u0013\u0010#\u001a\u00020$2\b\u0010%\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010&\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\'\u001a\u00020\u0007H\u00d6\u0001R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\f\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u0016\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0011\u00a8\u0006)"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/model/DoujinDetails;", "", "id", "", "nukeCode", "", "fullTitleEnglish", "", "fullTitleJapanese", "shortTitleEnglish", "absolutePath", "Ljava/io/File;", "folderName", "(Ljava/lang/Long;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/io/File;Ljava/lang/String;)V", "getAbsolutePath", "()Ljava/io/File;", "getFolderName", "()Ljava/lang/String;", "getFullTitleEnglish", "getFullTitleJapanese", "getId", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getNukeCode", "()I", "getShortTitleEnglish", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "(Ljava/lang/Long;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/io/File;Ljava/lang/String;)Lcom/flamyoad/tsukiviewer/core/model/DoujinDetails;", "equals", "", "other", "hashCode", "toString", "Companion", "core_debug"})
@androidx.room.Entity(tableName = "doujin_details")
@androidx.room.TypeConverters(value = {com.flamyoad.tsukiviewer.core.db.typeconverter.FolderConverter.class})
public final class DoujinDetails {
    @androidx.room.PrimaryKey(autoGenerate = true)
    @org.jetbrains.annotations.Nullable()
    private final java.lang.Long id = null;
    private final int nukeCode = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String fullTitleEnglish = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String fullTitleJapanese = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String shortTitleEnglish = null;
    @org.jetbrains.annotations.NotNull()
    private final java.io.File absolutePath = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String folderName = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.flamyoad.tsukiviewer.core.model.DoujinDetails.Companion Companion = null;
    
    public DoujinDetails(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, int nukeCode, @org.jetbrains.annotations.NotNull()
    java.lang.String fullTitleEnglish, @org.jetbrains.annotations.NotNull()
    java.lang.String fullTitleJapanese, @org.jetbrains.annotations.NotNull()
    java.lang.String shortTitleEnglish, @org.jetbrains.annotations.NotNull()
    java.io.File absolutePath, @org.jetbrains.annotations.NotNull()
    java.lang.String folderName) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long getId() {
        return null;
    }
    
    public final int getNukeCode() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFullTitleEnglish() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFullTitleJapanese() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getShortTitleEnglish() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.io.File getAbsolutePath() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getFolderName() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Long component1() {
        return null;
    }
    
    public final int component2() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.io.File component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.model.DoujinDetails copy(@org.jetbrains.annotations.Nullable()
    java.lang.Long id, int nukeCode, @org.jetbrains.annotations.NotNull()
    java.lang.String fullTitleEnglish, @org.jetbrains.annotations.NotNull()
    java.lang.String fullTitleJapanese, @org.jetbrains.annotations.NotNull()
    java.lang.String shortTitleEnglish, @org.jetbrains.annotations.NotNull()
    java.io.File absolutePath, @org.jetbrains.annotations.NotNull()
    java.lang.String folderName) {
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
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/model/DoujinDetails$Companion;", "", "()V", "getEmptyObject", "Lcom/flamyoad/tsukiviewer/core/model/DoujinDetails;", "dir", "Ljava/io/File;", "core_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.flamyoad.tsukiviewer.core.model.DoujinDetails getEmptyObject(@org.jetbrains.annotations.NotNull()
        java.io.File dir) {
            return null;
        }
    }
}