package com.flamyoad.tsukiviewer.core.di;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B)\b\u0007\u0012 \u0010\u0002\u001a\u001c\u0012\f\u0012\n\u0012\u0006\b\u0001\u0012\u00020\u00050\u0004\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00060\u0003\u00a2\u0006\u0002\u0010\u0007J%\u0010\b\u001a\u0002H\t\"\b\b\u0000\u0010\t*\u00020\u00052\f\u0010\n\u001a\b\u0012\u0004\u0012\u0002H\t0\u0004H\u0016\u00a2\u0006\u0002\u0010\u000bR(\u0010\u0002\u001a\u001c\u0012\f\u0012\n\u0012\u0006\b\u0001\u0012\u00020\u00050\u0004\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00060\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/di/ViewModelFactory;", "Landroidx/lifecycle/ViewModelProvider$Factory;", "viewModels", "", "Ljava/lang/Class;", "Landroidx/lifecycle/ViewModel;", "Ljavax/inject/Provider;", "(Ljava/util/Map;)V", "create", "T", "modelClass", "(Ljava/lang/Class;)Landroidx/lifecycle/ViewModel;", "core_debug"})
public final class ViewModelFactory implements androidx.lifecycle.ViewModelProvider.Factory {
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.Class<? extends androidx.lifecycle.ViewModel>, javax.inject.Provider<androidx.lifecycle.ViewModel>> viewModels = null;
    
    @javax.inject.Inject()
    public ViewModelFactory(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.Class<? extends androidx.lifecycle.ViewModel>, javax.inject.Provider<androidx.lifecycle.ViewModel>> viewModels) {
        super();
    }
    
    @java.lang.Override()
    @kotlin.Suppress(names = {"UNCHECKED_CAST"})
    @org.jetbrains.annotations.NotNull()
    public <T extends androidx.lifecycle.ViewModel>T create(@org.jetbrains.annotations.NotNull()
    java.lang.Class<T> modelClass) {
        return null;
    }
}