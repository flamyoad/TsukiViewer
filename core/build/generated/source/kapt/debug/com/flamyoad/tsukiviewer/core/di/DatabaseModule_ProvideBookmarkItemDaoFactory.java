package com.flamyoad.tsukiviewer.core.di;

import com.flamyoad.tsukiviewer.core.db.AppDatabase;
import com.flamyoad.tsukiviewer.core.db.dao.BookmarkItemDao;
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
public final class DatabaseModule_ProvideBookmarkItemDaoFactory implements Factory<BookmarkItemDao> {
  private final DatabaseModule module;

  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideBookmarkItemDaoFactory(DatabaseModule module,
      Provider<AppDatabase> databaseProvider) {
    this.module = module;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public BookmarkItemDao get() {
    return provideBookmarkItemDao(module, databaseProvider.get());
  }

  public static DatabaseModule_ProvideBookmarkItemDaoFactory create(DatabaseModule module,
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideBookmarkItemDaoFactory(module, databaseProvider);
  }

  public static BookmarkItemDao provideBookmarkItemDao(DatabaseModule instance,
      AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(instance.provideBookmarkItemDao(database));
  }
}
