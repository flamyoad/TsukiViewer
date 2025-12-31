package com.flamyoad.tsukiviewer.core.di;

import com.flamyoad.tsukiviewer.core.repository.BookmarkRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class RepositoryModule_ProvideBookmarkRepositoryFactory implements Factory<BookmarkRepository> {
  private final RepositoryModule module;

  private final Provider<BookmarkRepository> implProvider;

  public RepositoryModule_ProvideBookmarkRepositoryFactory(RepositoryModule module,
      Provider<BookmarkRepository> implProvider) {
    this.module = module;
    this.implProvider = implProvider;
  }

  @Override
  public BookmarkRepository get() {
    return provideBookmarkRepository(module, implProvider.get());
  }

  public static RepositoryModule_ProvideBookmarkRepositoryFactory create(RepositoryModule module,
      Provider<BookmarkRepository> implProvider) {
    return new RepositoryModule_ProvideBookmarkRepositoryFactory(module, implProvider);
  }

  public static BookmarkRepository provideBookmarkRepository(RepositoryModule instance,
      BookmarkRepository impl) {
    return Preconditions.checkNotNullFromProvides(instance.provideBookmarkRepository(impl));
  }
}
