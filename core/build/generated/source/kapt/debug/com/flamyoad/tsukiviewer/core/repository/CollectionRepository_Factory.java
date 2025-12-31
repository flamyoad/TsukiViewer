package com.flamyoad.tsukiviewer.core.repository;

import com.flamyoad.tsukiviewer.core.db.AppDatabase;
import com.flamyoad.tsukiviewer.core.db.dao.CollectionCriteriaDao;
import com.flamyoad.tsukiviewer.core.db.dao.CollectionDao;
import com.flamyoad.tsukiviewer.core.db.dao.CollectionDoujinDao;
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
public final class CollectionRepository_Factory implements Factory<CollectionRepository> {
  private final Provider<AppDatabase> dbProvider;

  private final Provider<CollectionDao> collectionDaoProvider;

  private final Provider<CollectionCriteriaDao> criteriaDaoProvider;

  private final Provider<CollectionDoujinDao> collectionDoujinDaoProvider;

  public CollectionRepository_Factory(Provider<AppDatabase> dbProvider,
      Provider<CollectionDao> collectionDaoProvider,
      Provider<CollectionCriteriaDao> criteriaDaoProvider,
      Provider<CollectionDoujinDao> collectionDoujinDaoProvider) {
    this.dbProvider = dbProvider;
    this.collectionDaoProvider = collectionDaoProvider;
    this.criteriaDaoProvider = criteriaDaoProvider;
    this.collectionDoujinDaoProvider = collectionDoujinDaoProvider;
  }

  @Override
  public CollectionRepository get() {
    return newInstance(dbProvider.get(), collectionDaoProvider.get(), criteriaDaoProvider.get(), collectionDoujinDaoProvider.get());
  }

  public static CollectionRepository_Factory create(Provider<AppDatabase> dbProvider,
      Provider<CollectionDao> collectionDaoProvider,
      Provider<CollectionCriteriaDao> criteriaDaoProvider,
      Provider<CollectionDoujinDao> collectionDoujinDaoProvider) {
    return new CollectionRepository_Factory(dbProvider, collectionDaoProvider, criteriaDaoProvider, collectionDoujinDaoProvider);
  }

  public static CollectionRepository newInstance(AppDatabase db, CollectionDao collectionDao,
      CollectionCriteriaDao criteriaDao, CollectionDoujinDao collectionDoujinDao) {
    return new CollectionRepository(db, collectionDao, criteriaDao, collectionDoujinDao);
  }
}
