package com.flamyoad.tsukiviewer.core.repository;

import com.flamyoad.tsukiviewer.core.db.dao.SearchHistoryDao;
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
public final class SearchHistoryRepository_Factory implements Factory<SearchHistoryRepository> {
  private final Provider<SearchHistoryDao> searchHistoryDaoProvider;

  public SearchHistoryRepository_Factory(Provider<SearchHistoryDao> searchHistoryDaoProvider) {
    this.searchHistoryDaoProvider = searchHistoryDaoProvider;
  }

  @Override
  public SearchHistoryRepository get() {
    return newInstance(searchHistoryDaoProvider.get());
  }

  public static SearchHistoryRepository_Factory create(
      Provider<SearchHistoryDao> searchHistoryDaoProvider) {
    return new SearchHistoryRepository_Factory(searchHistoryDaoProvider);
  }

  public static SearchHistoryRepository newInstance(SearchHistoryDao searchHistoryDao) {
    return new SearchHistoryRepository(searchHistoryDao);
  }
}
