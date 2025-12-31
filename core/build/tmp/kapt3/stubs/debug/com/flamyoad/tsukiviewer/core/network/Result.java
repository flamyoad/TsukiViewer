package com.flamyoad.tsukiviewer.core.network;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B3\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\tH\u00c6\u0003J\u000f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u00c6\u0003JA\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u00c6\u0001J\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010!\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\"\u001a\u00020\u0007H\u00d6\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u001c\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0016\u0010\b\u001a\u00020\t8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017\u00a8\u0006#"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/network/Result;", "", "nukeCode", "", "title", "Lcom/flamyoad/tsukiviewer/core/network/Title;", "scanlator", "", "upload_date", "", "tags", "", "Lcom/flamyoad/tsukiviewer/core/network/Tags;", "(ILcom/flamyoad/tsukiviewer/core/network/Title;Ljava/lang/String;JLjava/util/List;)V", "getNukeCode", "()I", "getScanlator", "()Ljava/lang/String;", "getTags", "()Ljava/util/List;", "getTitle", "()Lcom/flamyoad/tsukiviewer/core/network/Title;", "getUpload_date", "()J", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toString", "core_debug"})
public final class Result {
    @com.google.gson.annotations.SerializedName(value = "id")
    private final int nukeCode = 0;
    @com.google.gson.annotations.SerializedName(value = "title")
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.network.Title title = null;
    @com.google.gson.annotations.SerializedName(value = "scanlator")
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String scanlator = null;
    @com.google.gson.annotations.SerializedName(value = "upload_date")
    private final long upload_date = 0L;
    @com.google.gson.annotations.SerializedName(value = "tags")
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.flamyoad.tsukiviewer.core.network.Tags> tags = null;
    
    public Result(int nukeCode, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.network.Title title, @org.jetbrains.annotations.NotNull()
    java.lang.String scanlator, long upload_date, @org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.network.Tags> tags) {
        super();
    }
    
    public final int getNukeCode() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.network.Title getTitle() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getScanlator() {
        return null;
    }
    
    public final long getUpload_date() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.flamyoad.tsukiviewer.core.network.Tags> getTags() {
        return null;
    }
    
    public final int component1() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.network.Title component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component3() {
        return null;
    }
    
    public final long component4() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.flamyoad.tsukiviewer.core.network.Tags> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.network.Result copy(int nukeCode, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.network.Title title, @org.jetbrains.annotations.NotNull()
    java.lang.String scanlator, long upload_date, @org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.network.Tags> tags) {
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