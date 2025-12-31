package com.flamyoad.tsukiviewer.core.repository;

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
public final class TagRepository_Factory implements Factory<TagRepository> {
  private final Provider<TagDao> tagDaoProvider;

  public TagRepository_Factory(Provider<TagDao> tagDaoProvider) {
    this.tagDaoProvider = tagDaoProvider;
  }

  @Override
  public TagRepository get() {
    return newInstance(tagDaoProvider.get());
  }

  public static TagRepository_Factory create(Provider<TagDao> tagDaoProvider) {
    return new TagRepository_Factory(tagDaoProvider);
  }

  public static TagRepository newInstance(TagDao tagDao) {
    return new TagRepository(tagDao);
  }
}
