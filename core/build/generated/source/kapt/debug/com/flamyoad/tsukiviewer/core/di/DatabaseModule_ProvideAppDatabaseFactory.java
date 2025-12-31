package com.flamyoad.tsukiviewer.core.di;

import android.content.Context;
import com.flamyoad.tsukiviewer.core.db.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideAppDatabaseFactory implements Factory<AppDatabase> {
  private final DatabaseModule module;

  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideAppDatabaseFactory(DatabaseModule module,
      Provider<Context> contextProvider) {
    this.module = module;
    this.contextProvider = contextProvider;
  }

  @Override
  public AppDatabase get() {
    return provideAppDatabase(module, contextProvider.get());
  }

  public static DatabaseModule_ProvideAppDatabaseFactory create(DatabaseModule module,
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideAppDatabaseFactory(module, contextProvider);
  }

  public static AppDatabase provideAppDatabase(DatabaseModule instance, Context context) {
    return Preconditions.checkNotNullFromProvides(instance.provideAppDatabase(context));
  }
}
