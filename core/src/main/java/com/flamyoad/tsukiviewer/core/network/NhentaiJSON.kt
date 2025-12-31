package com.flamyoad.tsukiviewer.core.network

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

// List of POJOs required for Retrofit
@Keep
data class Metadata(
    @SerializedName("result") val result: List<Result>,
    @SerializedName("hasValue") val hasValue: Boolean = true) {

    fun getDoujinTitle(): String? {
        return result.first().title.english
    }

    fun getTags(): List<Tags> {
        return result.first().tags
    }
}

data class Result(
    @SerializedName("id") val nukeCode: Int,
    @SerializedName("title") val title: Title,
    @SerializedName("scanlator") val scanlator: String,
    @SerializedName("upload_date") val upload_date: Long,
    @SerializedName("tags") val tags: List<Tags>
)

data class Title (

    /* Example of JSON which contains null japanese field

    {"id":"328067",
    "media_id":"1728054",
    "title":{
        "english":"[The Jinshan] Sadistic Beauty | \u8650\u7f8e\u4eba Ch.52-53 [Chinese] [\u6c92\u6709\u6f22\u5316][Ongoing]",
        "japanese":null,
        "pretty":"Sadistic Beauty}
        . . . . . .
    }

     */
    @SerializedName("english") val english : String,
    @SerializedName("japanese") val japanese : String?, // This field might be null in JSON returned
    @SerializedName("pretty") val pretty : String? // Haven't encountered nulls in this field. But it might
)

data class Tags(
    @SerializedName("id") val id: Int,
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String,
    @SerializedName("url") val url: String,
    @SerializedName("count") val count: Int
)


