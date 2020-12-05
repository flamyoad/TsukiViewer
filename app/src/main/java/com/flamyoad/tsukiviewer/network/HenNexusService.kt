package com.flamyoad.tsukiviewer.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HenNexusService {

    companion object {
        const val baseUrl = "https://hentainexus.com/"
    }

    // Example: https://hentainexus.com/?q=sanjuurou
    @GET(".")
    fun getSearchResult(@Query("q")title: String): Call<ResponseBody>


    // Example: https://hentainexus.com/view/6293
    @GET("view/{id}")
    fun getPageUrl(@Path("id") pageId: Int): Call<ResponseBody>

    // Example: https://hentainexus.com/view/6293
    @GET("{link}")
    fun getPageUrl(@Path("link", encoded = true) relativeLink: String): Call<ResponseBody>
}