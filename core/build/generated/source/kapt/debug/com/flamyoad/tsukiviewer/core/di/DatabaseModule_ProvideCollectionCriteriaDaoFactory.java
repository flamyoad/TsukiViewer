package com.flamyoad.tsukiviewer.core.di;

import com.flamyoad.tsukiviewer.core.db.AppDatabase;
import com.flamyoad.tsukiviewer.core.db.dao.CollectionCriteriaDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DatabaseModule_ProvideCollectionCriteriaDaoFactory implements Factory<CollectionCriteriaDao> {
  private final DatabaseModule module;

  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideCollectionCriteriaDaoFactory(DatabaseModule module,
      Provider<AppDatabase> databaseProvider) {
    this.module = module;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public CollectionCriteriaDao get() {
    return provideCollectionCriteriaDao(module, databaseProvider.get());
  }

  public static DatabaseModule_ProvideCollectionCriteriaDaoFactory create(DatabaseModule module,
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideCollectionCriteriaDaoFactory(module, databaseProvider);
  }

  public static CollectionCriteriaDao provideCollectionCriteriaDao(DatabaseModule instance,
      AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(instance.provideCollectionCriteriaDao(database));
  }
}
