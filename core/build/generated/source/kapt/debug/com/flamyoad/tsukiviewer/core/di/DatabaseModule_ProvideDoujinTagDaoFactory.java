package com.flamyoad.tsukiviewer.core.di;

import com.flamyoad.tsukiviewer.core.db.AppDatabase;
import com.flamyoad.tsukiviewer.core.db.dao.DoujinTagsDao;
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
public final class DatabaseModule_ProvideDoujinTagDaoFactory implements Factory<DoujinTagsDao> {
  private final DatabaseModule module;

  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideDoujinTagDaoFactory(DatabaseModule module,
      Provider<AppDatabase> databaseProvider) {
    this.module = module;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public DoujinTagsDao get() {
    return provideDoujinTagDao(module, databaseProvider.get());
  }

  public static DatabaseModule_ProvideDoujinTagDaoFactory create(DatabaseModule module,
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideDoujinTagDaoFactory(module, databaseProvider);
  }

  public static DoujinTagsDao provideDoujinTagDao(DatabaseModule instance, AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(instance.provideDoujinTagDao(database));
  }
}
