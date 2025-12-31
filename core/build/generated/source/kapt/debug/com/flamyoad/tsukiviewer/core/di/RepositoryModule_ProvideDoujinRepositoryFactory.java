package com.flamyoad.tsukiviewer.core.di;

import com.flamyoad.tsukiviewer.core.repository.DoujinRepository;
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
public final class RepositoryModule_ProvideDoujinRepositoryFactory implements Factory<DoujinRepository> {
  private final RepositoryModule module;

  private final Provider<DoujinRepository> implProvider;

  public RepositoryModule_ProvideDoujinRepositoryFactory(RepositoryModule module,
      Provider<DoujinRepository> implProvider) {
    this.module = module;
    this.implProvider = implProvider;
  }

  @Override
  public DoujinRepository get() {
    return provideDoujinRepository(module, implProvider.get());
  }

  public static RepositoryModule_ProvideDoujinRepositoryFactory create(RepositoryModule module,
      Provider<DoujinRepository> implProvider) {
    return new RepositoryModule_ProvideDoujinRepositoryFactory(module, implProvider);
  }

  public static DoujinRepository provideDoujinRepository(RepositoryModule instance,
      DoujinRepository impl) {
    return Preconditions.checkNotNullFromProvides(instance.provideDoujinRepository(impl));
  }
}
