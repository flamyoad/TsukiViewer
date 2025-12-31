package com.flamyoad.tsukiviewer.core.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00b6\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B?\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\u0002\u0010\u0010J$\u0010.\u001a\u00020/2\u0006\u00100\u001a\u0002012\f\u00102\u001a\b\u0012\u0004\u0012\u00020403H\u0086@\u00a2\u0006\u0002\u00105J\b\u00106\u001a\u000207H\u0002J\u001e\u00108\u001a\u0002072\u0006\u00109\u001a\u00020:2\u0006\u0010;\u001a\u00020<H\u0082@\u00a2\u0006\u0002\u0010=J\u0016\u0010>\u001a\u0002072\u0006\u0010?\u001a\u00020@H\u0086@\u00a2\u0006\u0002\u0010AJ\u0010\u0010B\u001a\u00020C2\u0006\u0010D\u001a\u00020EH\u0002J\u0010\u0010F\u001a\u00020C2\u0006\u0010D\u001a\u00020EH\u0002J\u001e\u0010G\u001a\u00020C2\u0006\u0010D\u001a\u00020E2\f\u00102\u001a\b\u0012\u0004\u0012\u00020403H\u0002J$\u0010H\u001a\u0002072\u0006\u00100\u001a\u0002012\f\u00102\u001a\b\u0012\u0004\u0012\u00020403H\u0086@\u00a2\u0006\u0002\u00105J$\u0010I\u001a\u0002072\u0006\u0010?\u001a\u00020@2\f\u0010J\u001a\b\u0012\u0004\u0012\u00020<0KH\u0086@\u00a2\u0006\u0002\u0010LJ\u001e\u0010M\u001a\u00020E2\u0006\u0010N\u001a\u00020O2\u0006\u00100\u001a\u000201H\u0082@\u00a2\u0006\u0002\u0010PJ\u0010\u0010Q\u001a\u0002072\u0006\u0010R\u001a\u00020EH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082.\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u001b\u0010\u0019\u001a\u00020\u001a8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001d\u0010\u001e\u001a\u0004\b\u001b\u0010\u001cR\u000e\u0010\u001f\u001a\u00020 X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\"X\u0082.\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010$R\u001b\u0010%\u001a\u00020&8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b)\u0010\u001e\u001a\u0004\b\'\u0010(R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010+R\u0010\u0010,\u001a\u0004\u0018\u00010-X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006S"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/repository/MetadataRepository;", "", "context", "Landroid/content/Context;", "db", "Lcom/flamyoad/tsukiviewer/core/db/AppDatabase;", "pathDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/IncludedPathDao;", "doujinDetailsDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/DoujinDetailsDao;", "tagDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/TagDao;", "doujinTagDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/DoujinTagsDao;", "folderDao", "Lcom/flamyoad/tsukiviewer/core/db/dao/IncludedFolderDao;", "(Landroid/content/Context;Lcom/flamyoad/tsukiviewer/core/db/AppDatabase;Lcom/flamyoad/tsukiviewer/core/db/dao/IncludedPathDao;Lcom/flamyoad/tsukiviewer/core/db/dao/DoujinDetailsDao;Lcom/flamyoad/tsukiviewer/core/db/dao/TagDao;Lcom/flamyoad/tsukiviewer/core/db/dao/DoujinTagsDao;Lcom/flamyoad/tsukiviewer/core/db/dao/IncludedFolderDao;)V", "getDoujinDetailsDao", "()Lcom/flamyoad/tsukiviewer/core/db/dao/DoujinDetailsDao;", "getDoujinTagDao", "()Lcom/flamyoad/tsukiviewer/core/db/dao/DoujinTagsDao;", "fakkuService", "Lcom/flamyoad/tsukiviewer/core/network/api/FakkuService;", "getFolderDao", "()Lcom/flamyoad/tsukiviewer/core/db/dao/IncludedFolderDao;", "henNexusParser", "Lcom/flamyoad/tsukiviewer/core/parser/HenNexusParser;", "getHenNexusParser", "()Lcom/flamyoad/tsukiviewer/core/parser/HenNexusParser;", "henNexusParser$delegate", "Lkotlin/Lazy;", "henNexusService", "Lcom/flamyoad/tsukiviewer/core/network/api/HenNexusService;", "nhService", "Lcom/flamyoad/tsukiviewer/core/network/api/NHService;", "getPathDao", "()Lcom/flamyoad/tsukiviewer/core/db/dao/IncludedPathDao;", "regex", "Lkotlin/text/Regex;", "getRegex", "()Lkotlin/text/Regex;", "regex$delegate", "getTagDao", "()Lcom/flamyoad/tsukiviewer/core/db/dao/TagDao;", "toast", "Landroid/widget/Toast;", "fetchMetadata", "Lcom/flamyoad/tsukiviewer/core/network/FetchHistory;", "dir", "Ljava/io/File;", "sources", "Ljava/util/EnumSet;", "Lcom/flamyoad/tsukiviewer/core/model/Source;", "(Ljava/io/File;Ljava/util/EnumSet;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "initializeNetwork", "", "insertTagElseIncrement", "doujinId", "", "tag", "Lcom/flamyoad/tsukiviewer/core/model/Tag;", "(JLcom/flamyoad/tsukiviewer/core/model/Tag;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "removeMetadata", "doujinDetails", "Lcom/flamyoad/tsukiviewer/core/model/DoujinDetails;", "(Lcom/flamyoad/tsukiviewer/core/model/DoujinDetails;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "requestFromHenNexus", "Lcom/flamyoad/tsukiviewer/core/network/FetchResult;", "fullTitle", "", "requestFromNhentai", "requestMetadata", "resetTags", "saveEditedMetadata", "tags", "", "(Lcom/flamyoad/tsukiviewer/core/model/DoujinDetails;Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveMetadata", "metadata", "Lcom/flamyoad/tsukiviewer/core/network/Metadata;", "(Lcom/flamyoad/tsukiviewer/core/network/Metadata;Ljava/io/File;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "showToast", "message", "core_debug"})
public final class MetadataRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.AppDatabase db = null;
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao pathDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.dao.DoujinDetailsDao doujinDetailsDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.dao.TagDao tagDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.dao.DoujinTagsDao doujinTagDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.flamyoad.tsukiviewer.core.db.dao.IncludedFolderDao folderDao = null;
    private com.flamyoad.tsukiviewer.core.network.api.NHService nhService;
    private com.flamyoad.tsukiviewer.core.network.api.HenNexusService henNexusService;
    private com.flamyoad.tsukiviewer.core.network.api.FakkuService fakkuService;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy henNexusParser$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy regex$delegate = null;
    @org.jetbrains.annotations.Nullable()
    private android.widget.Toast toast;
    
    @javax.inject.Inject()
    public MetadataRepository(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.AppDatabase db, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao pathDao, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.dao.DoujinDetailsDao doujinDetailsDao, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.dao.TagDao tagDao, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.dao.DoujinTagsDao doujinTagDao, @org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.db.dao.IncludedFolderDao folderDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao getPathDao() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.DoujinDetailsDao getDoujinDetailsDao() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.TagDao getTagDao() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.DoujinTagsDao getDoujinTagDao() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.db.dao.IncludedFolderDao getFolderDao() {
        return null;
    }
    
    private final com.flamyoad.tsukiviewer.core.parser.HenNexusParser getHenNexusParser() {
        return null;
    }
    
    private final kotlin.text.Regex getRegex() {
        return null;
    }
    
    private final void initializeNetwork() {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object fetchMetadata(@org.jetbrains.annotations.NotNull()
    java.io.File dir, @org.jetbrains.annotations.NotNull()
    java.util.EnumSet<com.flamyoad.tsukiviewer.core.model.Source> sources, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.flamyoad.tsukiviewer.core.network.FetchHistory> $completion) {
        return null;
    }
    
    private final com.flamyoad.tsukiviewer.core.network.FetchResult requestMetadata(java.lang.String fullTitle, java.util.EnumSet<com.flamyoad.tsukiviewer.core.model.Source> sources) {
        return null;
    }
    
    private final com.flamyoad.tsukiviewer.core.network.FetchResult requestFromNhentai(java.lang.String fullTitle) {
        return null;
    }
    
    private final com.flamyoad.tsukiviewer.core.network.FetchResult requestFromHenNexus(java.lang.String fullTitle) {
        return null;
    }
    
    private final java.lang.Object saveMetadata(com.flamyoad.tsukiviewer.core.network.Metadata metadata, java.io.File dir, kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveEditedMetadata(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.DoujinDetails doujinDetails, @org.jetbrains.annotations.NotNull()
    java.util.List<com.flamyoad.tsukiviewer.core.model.Tag> tags, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object insertTagElseIncrement(long doujinId, com.flamyoad.tsukiviewer.core.model.Tag tag, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object removeMetadata(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.model.DoujinDetails doujinDetails, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object resetTags(@org.jetbrains.annotations.NotNull()
    java.io.File dir, @org.jetbrains.annotations.NotNull()
    java.util.EnumSet<com.flamyoad.tsukiviewer.core.model.Source> sources, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final void showToast(java.lang.String message) {
    }
}