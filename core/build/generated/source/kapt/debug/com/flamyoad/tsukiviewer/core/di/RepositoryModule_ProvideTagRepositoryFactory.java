package com.flamyoad.tsukiviewer.core.di;

import com.flamyoad.tsukiviewer.core.repository.TagRepository;
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
public final class RepositoryModule_ProvideTagRepositoryFactory implements Factory<TagRepository> {
  private final RepositoryModule module;

  private final Provider<TagRepository> implProvider;

  public RepositoryModule_ProvideTagRepositoryFactory(RepositoryModule module,
      Provider<TagRepository> implProvider) {
    this.module = module;
    this.implProvider = implProvider;
  }

  @Override
  public TagRepository get() {
    return provideTagRepository(module, implProvider.get());
  }

  public static RepositoryModule_ProvideTagRepositoryFactory create(RepositoryModule module,
      Provider<TagRepository> implProvider) {
    return new RepositoryModule_ProvideTagRepositoryFactory(module, implProvider);
  }

  public static TagRepository provideTagRepository(RepositoryModule instance, TagRepository impl) {
    return Preconditions.checkNotNullFromProvides(instance.provideTagRepository(impl));
  }
}
