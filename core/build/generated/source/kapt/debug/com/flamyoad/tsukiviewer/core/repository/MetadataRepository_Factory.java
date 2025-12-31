package com.flamyoad.tsukiviewer.core.repository;

import android.content.Context;
import com.flamyoad.tsukiviewer.core.db.AppDatabase;
import com.flamyoad.tsukiviewer.core.db.dao.DoujinDetailsDao;
import com.flamyoad.tsukiviewer.core.db.dao.DoujinTagsDao;
import com.flamyoad.tsukiviewer.core.db.dao.IncludedFolderDao;
import com.flamyoad.tsukiviewer.core.db.dao.IncludedPathDao;
import com.flamyoad.tsukiviewer.core.db.dao.TagDao;
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
public final class MetadataRepository_Factory implements Factory<MetadataRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<AppDatabase> dbProvider;

  private final Provider<IncludedPathDao> pathDaoProvider;

  private final Provider<DoujinDetailsDao> doujinDetailsDaoProvider;

  private final Provider<TagDao> tagDaoProvider;

  private final Provider<DoujinTagsDao> doujinTagDaoProvider;

  private final Provider<IncludedFolderDao> folderDaoProvider;

  public MetadataRepository_Factory(Provider<Context> contextProvider,
      Provider<AppDatabase> dbProvider, Provider<IncludedPathDao> pathDaoProvider,
      Provider<DoujinDetailsDao> doujinDetailsDaoProvider, Provider<TagDao> tagDaoProvider,
      Provider<DoujinTagsDao> doujinTagDaoProvider, Provider<IncludedFolderDao> folderDaoProvider) {
    this.contextProvider = contextProvider;
    this.dbProvider = dbProvider;
    this.pathDaoProvider = pathDaoProvider;
    this.doujinDetailsDaoProvider = doujinDetailsDaoProvider;
    this.tagDaoProvider = tagDaoProvider;
    this.doujinTagDaoProvider = doujinTagDaoProvider;
    this.folderDaoProvider = folderDaoProvider;
  }

  @Override
  public MetadataRepository get() {
    return newInstance(contextProvider.get(), dbProvider.get(), pathDaoProvider.get(), doujinDetailsDaoProvider.get(), tagDaoProvider.get(), doujinTagDaoProvider.get(), folderDaoProvider.get());
  }

  public static MetadataRepository_Factory create(Provider<Context> contextProvider,
      Provider<AppDatabase> dbProvider, Provider<IncludedPathDao> pathDaoProvider,
      Provider<DoujinDetailsDao> doujinDetailsDaoProvider, Provider<TagDao> tagDaoProvider,
      Provider<DoujinTagsDao> doujinTagDaoProvider, Provider<IncludedFolderDao> folderDaoProvider) {
    return new MetadataRepository_Factory(contextProvider, dbProvider, pathDaoProvider, doujinDetailsDaoProvider, tagDaoProvider, doujinTagDaoProvider, folderDaoProvider);
  }

  public static MetadataRepository newInstance(Context context, AppDatabase db,
      IncludedPathDao pathDao, DoujinDetailsDao doujinDetailsDao, TagDao tagDao,
      DoujinTagsDao doujinTagDao, IncludedFolderDao folderDao) {
    return new MetadataRepository(context, db, pathDao, doujinDetailsDao, tagDao, doujinTagDao, folderDao);
  }
}
