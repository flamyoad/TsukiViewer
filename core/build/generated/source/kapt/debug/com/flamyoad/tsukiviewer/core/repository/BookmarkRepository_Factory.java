package com.flamyoad.tsukiviewer.core.repository;

import com.flamyoad.tsukiviewer.core.db.AppDatabase;
import com.flamyoad.tsukiviewer.core.db.dao.BookmarkGroupDao;
import com.flamyoad.tsukiviewer.core.db.dao.BookmarkItemDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class BookmarkRepository_Factory implements Factory<BookmarkRepository> {
  private final Provider<AppDatabase> dbProvider;

  private final Provider<BookmarkGroupDao> groupDaoProvider;

  private final Provider<BookmarkItemDao> itemDaoProvider;

  public BookmarkRepository_Factory(Provider<AppDatabase> dbProvider,
      Provider<BookmarkGroupDao> groupDaoProvider, Provider<BookmarkItemDao> itemDaoProvider) {
    this.dbProvider = dbProvider;
    this.groupDaoProvider = groupDaoProvider;
    this.itemDaoProvider = itemDaoProvider;
  }

  @Override
  public BookmarkRepository get() {
    return newInstance(dbProvider.get(), groupDaoProvider.get(), itemDaoProvider.get());
  }

  public static BookmarkRepository_Factory create(Provider<AppDatabase> dbProvider,
      Provider<BookmarkGroupDao> groupDaoProvider, Provider<BookmarkItemDao> itemDaoProvider) {
    return new BookmarkRepository_Factory(dbProvider, groupDaoProvider, itemDaoProvider);
  }

  public static BookmarkRepository newInstance(AppDatabase db, BookmarkGroupDao groupDao,
      BookmarkItemDao itemDao) {
    return new BookmarkRepository(db, groupDao, itemDao);
  }
}
