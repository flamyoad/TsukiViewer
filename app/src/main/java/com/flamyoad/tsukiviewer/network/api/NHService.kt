package com.flamyoad.tsukiviewer.network.api

import com.flamyoad.tsukiviewer.network.Metadata
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NHService {

    companion object {
        const val baseUrl = "https://nhentai.net/"
    }

    /* The query parameter needs to be wrapped with double quotes to let the server know that we want exact title search

       Exact searches can be performed by wrapping terms in double quotes.
       For example, "big breasts" only matches galleries with "big breasts" somewhere in the title or in tags.

       Documentation link: https://nhentai.net/info/
    */

    // %22 decodes to " (double quote)
    @GET("api/galleries/search")
    fun getMetadata(@Query("query") fullTitle: String): Call<Metadata>
}