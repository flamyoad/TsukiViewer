package com.flamyoad.tsukiviewer.network

import com.google.gson.annotations.SerializedName

// List of POJOs required for Retrofit
data class Metadata(
    @SerializedName("result") val result: List<Result>
)

data class Result(
    @SerializedName("id") val nukeCode: Int,
    @SerializedName("title") val title: Title,
    @SerializedName("scanlator") val scanlator: String,
    @SerializedName("upload_date") val upload_date: Long,
    @SerializedName("tags") val tags: List<Tags>
)

data class Title (

    @SerializedName("english") val english : String,
    @SerializedName("japanese") val japanese : String,
    @SerializedName("pretty") val pretty : String
)

data class Tags(
    @SerializedName("id") val id: Int,
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String,
    @SerializedName("count") val count: Int
)


