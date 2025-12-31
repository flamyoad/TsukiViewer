package com.flamyoad.tsukiviewer.core.di;

import androidx.lifecycle.ViewModel;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import java.util.Map;
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
public final class ViewModelFactory_Factory implements Factory<ViewModelFactory> {
  private final Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> viewModelsProvider;

  public ViewModelFactory_Factory(
      Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> viewModelsProvider) {
    this.viewModelsProvider = viewModelsProvider;
  }

  @Override
  public ViewModelFactory get() {
    return newInstance(viewModelsProvider.get());
  }

  public static ViewModelFactory_Factory create(
      Provider<Map<Class<? extends ViewModel>, Provider<ViewModel>>> viewModelsProvider) {
    return new ViewModelFactory_Factory(viewModelsProvider);
  }

  public static ViewModelFactory newInstance(
      Map<Class<? extends ViewModel>, Provider<ViewModel>> viewModels) {
    return new ViewModelFactory(viewModels);
  }
}
