package com.flamyoad.tsukiviewer.core.network.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\bf\u0018\u0000 \u000b2\u00020\u0001:\u0001\u000bJ\u0018\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\'J\u0018\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0007\u001a\u00020\bH\'J\u0018\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\n\u001a\u00020\bH\'\u00a8\u0006\f"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/network/api/HenNexusService;", "", "getPageUrl", "Lretrofit2/Call;", "Lokhttp3/ResponseBody;", "pageId", "", "relativeLink", "", "getSearchResult", "title", "Companion", "core_debug"})
public abstract interface HenNexusService {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String baseUrl = "https://hentainexus.com/";
    @org.jetbrains.annotations.NotNull()
    public static final com.flamyoad.tsukiviewer.core.network.api.HenNexusService.Companion Companion = null;
    
    @retrofit2.http.GET(value = ".")
    @org.jetbrains.annotations.NotNull()
    public abstract retrofit2.Call<okhttp3.ResponseBody> getSearchResult(@retrofit2.http.Query(value = "q")
    @org.jetbrains.annotations.NotNull()
    java.lang.String title);
    
    @retrofit2.http.GET(value = "view/{id}")
    @org.jetbrains.annotations.NotNull()
    public abstract retrofit2.Call<okhttp3.ResponseBody> getPageUrl(@retrofit2.http.Path(value = "id")
    int pageId);
    
    @retrofit2.http.GET(value = "{link}")
    @org.jetbrains.annotations.NotNull()
    public abstract retrofit2.Call<okhttp3.ResponseBody> getPageUrl(@retrofit2.http.Path(value = "link", encoded = true)
    @org.jetbrains.annotations.NotNull()
    java.lang.String relativeLink);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/network/api/HenNexusService$Companion;", "", "()V", "baseUrl", "", "core_debug"})
    public static final class Companion {
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String baseUrl = "https://hentainexus.com/";
        
        private Companion() {
            super();
        }
    }
}