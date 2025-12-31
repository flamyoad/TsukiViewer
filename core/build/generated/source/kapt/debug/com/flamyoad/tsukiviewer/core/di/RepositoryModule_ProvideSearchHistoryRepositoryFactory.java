package com.flamyoad.tsukiviewer.core.di;

import com.flamyoad.tsukiviewer.core.repository.SearchHistoryRepository;
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
public final class RepositoryModule_ProvideSearchHistoryRepositoryFactory implements Factory<SearchHistoryRepository> {
  private final RepositoryModule module;

  private final Provider<SearchHistoryRepository> implProvider;

  public RepositoryModule_ProvideSearchHistoryRepositoryFactory(RepositoryModule module,
      Provider<SearchHistoryRepository> implProvider) {
    this.module = module;
    this.implProvider = implProvider;
  }

  @Override
  public SearchHistoryRepository get() {
    return provideSearchHistoryRepository(module, implProvider.get());
  }

  public static RepositoryModule_ProvideSearchHistoryRepositoryFactory create(
      RepositoryModule module, Provider<SearchHistoryRepository> implProvider) {
    return new RepositoryModule_ProvideSearchHistoryRepositoryFactory(module, implProvider);
  }

  public static SearchHistoryRepository provideSearchHistoryRepository(RepositoryModule instance,
      SearchHistoryRepository impl) {
    return Preconditions.checkNotNullFromProvides(instance.provideSearchHistoryRepository(impl));
  }
}
