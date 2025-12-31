package com.flamyoad.tsukiviewer.core.network.api;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\bf\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007J\u0018\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\'\u00a8\u0006\b"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/network/api/FakkuService;", "", "getSearchResult", "Lretrofit2/Call;", "Lokhttp3/ResponseBody;", "encodedTitle", "", "Companion", "core_debug"})
public abstract interface FakkuService {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String baseUrl = "https://www.fakku.net";
    @org.jetbrains.annotations.NotNull()
    public static final com.flamyoad.tsukiviewer.core.network.api.FakkuService.Companion Companion = null;
    
    @retrofit2.http.GET(value = "search/{title}")
    @org.jetbrains.annotations.NotNull()
    public abstract retrofit2.Call<okhttp3.ResponseBody> getSearchResult(@retrofit2.http.Path(value = "title")
    @org.jetbrains.annotations.NotNull()
    java.lang.String encodedTitle);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/flamyoad/tsukiviewer/core/network/api/FakkuService$Companion;", "", "()V", "baseUrl", "", "core_debug"})
    public static final class Companion {
        @org.jetbrains.annotations.NotNull()
        public static final java.lang.String baseUrl = "https://www.fakku.net";
        
        private Companion() {
            super();
        }
    }
}