package com.flamyoad.tsukiviewer.core.di;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0004H\u0007J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0005\u001a\u00020\u0007H\u0007J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\u0005\u001a\u00020\tH\u0007J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\u000bH\u0007J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u0005\u001a\u00020\rH\u0007J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0005\u001a\u00020\u000fH\u0007\u00a8\u0006\u0010"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/di/RepositoryModule;", "", "()V", "provideBookmarkRepository", "Lcom/flamyoad/tsukiviewer/core/repository/BookmarkRepository;", "impl", "provideCollectionRepository", "Lcom/flamyoad/tsukiviewer/core/repository/CollectionRepository;", "provideDoujinRepository", "Lcom/flamyoad/tsukiviewer/core/repository/DoujinRepository;", "provideMetadataRepository", "Lcom/flamyoad/tsukiviewer/core/repository/MetadataRepository;", "provideSearchHistoryRepository", "Lcom/flamyoad/tsukiviewer/core/repository/SearchHistoryRepository;", "provideTagRepository", "Lcom/flamyoad/tsukiviewer/core/repository/TagRepository;", "core_debug"})
public final class RepositoryModule {
    
    public RepositoryModule() {
        super();
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.repository.BookmarkRepository provideBookmarkRepository(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.repository.BookmarkRepository impl) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.repository.CollectionRepository provideCollectionRepository(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.repository.CollectionRepository impl) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.repository.DoujinRepository provideDoujinRepository(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.repository.DoujinRepository impl) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.repository.MetadataRepository provideMetadataRepository(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.repository.MetadataRepository impl) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.repository.SearchHistoryRepository provideSearchHistoryRepository(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.repository.SearchHistoryRepository impl) {
        return null;
    }
    
    @dagger.Provides()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public final com.flamyoad.tsukiviewer.core.repository.TagRepository provideTagRepository(@org.jetbrains.annotations.NotNull()
    com.flamyoad.tsukiviewer.core.repository.TagRepository impl) {
        return null;
    }
}