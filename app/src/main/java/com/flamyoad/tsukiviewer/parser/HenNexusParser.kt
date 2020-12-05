package com.flamyoad.tsukiviewer.parser

import com.flamyoad.tsukiviewer.network.Metadata
import com.flamyoad.tsukiviewer.network.Result
import com.flamyoad.tsukiviewer.network.Tags
import com.flamyoad.tsukiviewer.network.Title
import org.jsoup.Jsoup

class HenNexusParser {

    private val baseUrl = "https://hentainexus.com"

    // Returns the link of the first item in result
    fun getFirstItemInList(html: String): String {
        val document = Jsoup.parse(html)

        val itemList = document.selectFirst(".container > .columns") ?: return ""

        val firstItem = itemList.selectFirst("div") ?: return ""

        // Example value: "/view/8808"
        val relativeLink = firstItem.select("a").attr("href")

        if (relativeLink.isNotBlank()) {
            return relativeLink
        } else {
            return ""
        }
    }

    // Returns the title, tags found in+ the HenNexus doujin
    //todo: Surround this method with try/catch..
    fun parseItem(html: String): Metadata {
        val document = Jsoup.parse(html)

        val englishTitleContainer = document.selectFirst("h1.title")

        // Means no result is found
        if (englishTitleContainer == null) {
            return Metadata(emptyList(), false)
        }

        val englishTitle = document.selectFirst("h1.title").text() ?: ""

        val table = document.selectFirst("table.view-page-details")
        val tableRows = table.select("tr")

        var artistName = ""
        var language = ""
        var magazine = ""
        var parody = ""
        var publisher = ""

        for (row in tableRows) {
            // https://stackoverflow.com/questions/7985791/why-jsoup-cannot-select-td-element
            // Jsoup is a HTML5 compliant parser. It cannot detect a <td> that is not inside a <table>
            val correctedHtml = "<table>" + row.html() + "</table>"
            val correctedRow = Jsoup.parse(correctedHtml)

            val rowLabel = correctedRow.selectFirst(".viewcolumn").text()
            val rowContainer = correctedRow.selectFirst("a")

            // 'Pages' row does not have link and will return null
            if (rowContainer != null) {
                // Make the string lowercase to be consistent with NHentai e.g. Big Breasts -> big breasts
                val rowValue = rowContainer.text().toLowerCase()
                when (rowLabel) {
                    "Artist" -> artistName = rowValue
                    "Language" -> language = rowValue
                    "Magazine" -> magazine = rowValue
                    "Parody" -> parody = rowValue
                    "Publisher" -> publisher = rowValue
                }
            }
        }

        val spans = tableRows.select("span.tag")

        val tagList =
            spans.map { Tags(id = 0, type = "tag", name = it.text(), count = 0, url = "") }
                .toMutableList()

        tagList.add(Tags(id = 0, type ="artist", name = artistName, count = 0, url = ""))
        tagList.add(Tags(id = 0, type ="language", name = language, count = 0, url = ""))
        tagList.add(Tags(id = 0, type ="group", name = magazine, count = 0, url = ""))
        tagList.add(Tags(id = 0, type ="parody", name = parody, count = 0, url = ""))
        tagList.add(Tags(id = 0, type ="group", name = publisher, count = 0, url = ""))

        val result = Result(
            nukeCode =  0,
            title = Title(english = englishTitle, japanese = "", pretty = ""),
            scanlator = "",
            upload_date = 0,
            tags = tagList
        )

        val metadata = Metadata(listOf(result))
        return metadata
    }
}