package com.flamyoad.tsukiviewer.network.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FakkuService {

    companion object {
        const val baseUrl = "https://www.fakku.net"
    }

    // Example: https://www.fakku.net/search/%20Occult%20Cupid
    @GET("search/{title}")
    fun getSearchResult(@Path("title") encodedTitle: String): Call<ResponseBody>
}