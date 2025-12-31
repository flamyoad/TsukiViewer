package com.flamyoad.tsukiviewer.core.repository;

import android.app.Application;
import com.flamyoad.tsukiviewer.core.db.dao.DoujinDetailsDao;
import com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao;
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
public final class DoujinRepository_Factory implements Factory<DoujinRepository> {
  private final Provider<Application> applicationProvider;

  private final Provider<DoujinDetailsDao> doujinDetailsDaoProvider;

  private final Provider<IncludedPathDao> pathDaoProvider;

  public DoujinRepository_Factory(Provider<Application> applicationProvider,
      Provider<DoujinDetailsDao> doujinDetailsDaoProvider,
      Provider<IncludedPathDao> pathDaoProvider) {
    this.applicationProvider = applicationProvider;
    this.doujinDetailsDaoProvider = doujinDetailsDaoProvider;
    this.pathDaoProvider = pathDaoProvider;
  }

  @Override
  public DoujinRepository get() {
    return newInstance(applicationProvider.get(), doujinDetailsDaoProvider.get(), pathDaoProvider.get());
  }

  public static DoujinRepository_Factory create(Provider<Application> applicationProvider,
      Provider<DoujinDetailsDao> doujinDetailsDaoProvider,
      Provider<IncludedPathDao> pathDaoProvider) {
    return new DoujinRepository_Factory(applicationProvider, doujinDetailsDaoProvider, pathDaoProvider);
  }

  public static DoujinRepository newInstance(Application application,
      DoujinDetailsDao doujinDetailsDao, IncludedPathDao pathDao) {
    return new DoujinRepository(application, doujinDetailsDao, pathDao);
  }
}
