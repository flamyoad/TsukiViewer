package com.flamyoad.tsukiviewer.core.di;

import com.flamyoad.tsukiviewer.core.repository.CollectionRepository;
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
public final class RepositoryModule_ProvideCollectionRepositoryFactory implements Factory<CollectionRepository> {
  private final RepositoryModule module;

  private final Provider<CollectionRepository> implProvider;

  public RepositoryModule_ProvideCollectionRepositoryFactory(RepositoryModule module,
      Provider<CollectionRepository> implProvider) {
    this.module = module;
    this.implProvider = implProvider;
  }

  @Override
  public CollectionRepository get() {
    return provideCollectionRepository(module, implProvider.get());
  }

  public static RepositoryModule_ProvideCollectionRepositoryFactory create(RepositoryModule module,
      Provider<CollectionRepository> implProvider) {
    return new RepositoryModule_ProvideCollectionRepositoryFactory(module, implProvider);
  }

  public static CollectionRepository provideCollectionRepository(RepositoryModule instance,
      CollectionRepository impl) {
    return Preconditions.checkNotNullFromProvides(instance.provideCollectionRepository(impl));
  }
}
