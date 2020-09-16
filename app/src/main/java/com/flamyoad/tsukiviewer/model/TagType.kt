package com.flamyoad.tsukiviewer.model

enum class TagType(private val shortName: String) {
    All("All"),

    Parodies("parody"),
    Characters("character"),
    Tags("tag"),
    Artists("artist"),
    Groups("group"),
    Languages("language"),
    Categories("category");

    fun getLowerCaseName(): String {
        return shortName
    }
}
