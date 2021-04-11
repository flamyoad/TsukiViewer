package com.flamyoad.tsukiviewer.model

import com.flamyoad.tsukiviewer.R

enum class Source(val drawableId: Int, val readableName: String, val secondPerRequest: Int) {
    NHentai(R.drawable.fav_nhentai_black, "NHentai", 2),
//    HentaiNexus(R.drawable.hentai_nexus_logo, "Hentai Nexus", 5),
}