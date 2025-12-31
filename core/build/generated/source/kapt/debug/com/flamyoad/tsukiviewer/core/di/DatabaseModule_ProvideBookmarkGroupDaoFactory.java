package com.flamyoad.tsukiviewer.core.di;

import com.flamyoad.tsukiviewer.core.db.AppDatabase;
import com.flamyoad.tsukiviewer.core.db.dao.BookmarkGroupDao;
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
public final class DatabaseModule_ProvideBookmarkGroupDaoFactory implements Factory<BookmarkGroupDao> {
  private final DatabaseModule module;

  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideBookmarkGroupDaoFactory(DatabaseModule module,
      Provider<AppDatabase> databaseProvider) {
    this.module = module;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public BookmarkGroupDao get() {
    return provideBookmarkGroupDao(module, databaseProvider.get());
  }

  public static DatabaseModule_ProvideBookmarkGroupDaoFactory create(DatabaseModule module,
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideBookmarkGroupDaoFactory(module, databaseProvider);
  }

  public static BookmarkGroupDao provideBookmarkGroupDao(DatabaseModule instance,
      AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(instance.provideBookmarkGroupDao(database));
  }
}
