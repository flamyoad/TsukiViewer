package com.flamyoad.tsukiviewer.core.di;

import com.flamyoad.tsukiviewer.core.repository.MetadataRepository;
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
public final class RepositoryModule_ProvideMetadataRepositoryFactory implements Factory<MetadataRepository> {
  private final RepositoryModule module;

  private final Provider<MetadataRepository> implProvider;

  public RepositoryModule_ProvideMetadataRepositoryFactory(RepositoryModule module,
      Provider<MetadataRepository> implProvider) {
    this.module = module;
    this.implProvider = implProvider;
  }

  @Override
  public MetadataRepository get() {
    return provideMetadataRepository(module, implProvider.get());
  }

  public static RepositoryModule_ProvideMetadataRepositoryFactory create(RepositoryModule module,
      Provider<MetadataRepository> implProvider) {
    return new RepositoryModule_ProvideMetadataRepositoryFactory(module, implProvider);
  }

  public static MetadataRepository provideMetadataRepository(RepositoryModule instance,
      MetadataRepository impl) {
    return Preconditions.checkNotNullFromProvides(instance.provideMetadataRepository(impl));
  }
}
