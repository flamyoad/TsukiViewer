package com.flamyoad.tsukiviewer.core.repository

import com.flamyoad.tsukiviewer.core.model.DoujinDetails
import com.flamyoad.tsukiviewer.core.model.DoujinDetailsWithTags
import com.flamyoad.tsukiviewer.core.model.Tag
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class RelatedMangaTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private val series = Tag(type = "parody", name = "Moon Library")
    private val character = Tag(type = "character", name = "Luna")
    private val artist = Tag(type = "artist", name = "Mika")
    private val generalTags = listOf(Tag(type = "tag", name = "Mystery"), Tag(type = "tag", name = "Time travel"))

    @Test
    fun ranksByStrongestSignalAndExcludesUnrelatedTitles() = runBlocking {
        val source = metadata("The Silent Library", listOf(series, character, artist) + generalTags)
        val candidates = listOf(
            candidate("Clockwork", generalTags),
            candidate("Moon Garden", listOf(artist)),
            candidate("[Release] The Silent Library Vol. 2 [EN]"),
            candidate("Midnight Station", listOf(character)),
            candidate("Blue Horizon", listOf(series)),
            candidate("Paper Lantern", listOf(series, character)),
            candidate("Forest Path", listOf(series.copy(type = "group"))),
            candidate("Midnight Train", generalTags.take(1))
        )

        assertEquals(
            RelatedMangaReason.entries.filterNot {
                it == RelatedMangaReason.SIMILAR_COVER || it == RelatedMangaReason.SHARED_FOLDER_TAG
            },
            rankRelatedManga(File("The Silent Library"), source, candidates).map { it.reason }
        )
    }

    @Test
    fun comparesFolderNamesAndJapaneseTitlesWithoutRequiringTags() = runBlocking {
        val folderMatch = candidate("[Release] Ｍｏｏｎ Garden Vol. 12")
        val japaneseMatch = candidate("無限図書館 第２巻")
        val source = metadata("Moon Garden", emptyList(), japaneseTitle = "無限図書館")

        assertEquals(2, rankRelatedManga(File("Moon Garden"), source, listOf(folderMatch, japaneseMatch)).size)
        assertEquals(
            listOf(folderMatch.directory),
            rankRelatedManga(File("Moon Garden"), null, listOf(folderMatch, japaneseMatch)).map { it.directory }
        )
    }

    @Test
    fun matchesNumberedTitlesWhenWordOverlapAloneIsTooLow() = runBlocking {
        val candidates = listOf(candidate("Sample Manga 14"), candidate("Sample Story 13"))

        val matches = rankRelatedManga(File("Sample Manga 13"), null, candidates)

        assertEquals(listOf("Sample Manga 14"), matches.map { it.title })
        assertEquals(RelatedMangaReason.SIMILAR_TITLE, matches.single().reason)
    }

    @Test
    fun ranksSharedBracketNamesWithoutImportedMetadata() = runBlocking {
        val source = File("［Moon Library］ [Artist: Mika] Quiet Skies [English] [Digital]")
        val twoNames = candidate("[MIKA] Midnight Station [Moon Library]")
        val oneName = candidate("[Moon Library] Paper Lantern")
        val commonReleaseMarkers = candidate("[English] Forest Path [Digital]")
        val matches = rankRelatedManga(source, null, listOf(commonReleaseMarkers, oneName, twoNames))

        assertEquals(listOf(twoNames.directory, oneName.directory), matches.map { it.directory })
        assertTrue(matches.all { it.reason == RelatedMangaReason.SHARED_FOLDER_TAG })
    }

    @Test
    fun connectsFolderTagsToImportedMetadataInEitherDirection() = runBlocking {
        val named = candidate("[Moon Library] Quiet Skies")
        val tagged = candidate("Paper Lantern", listOf(series))

        for ((source, target) in listOf(named to tagged, tagged to named)) {
            val match = rankRelatedManga(source.directory, source.metadata, listOf(target)).single()
            assertEquals(target.directory, match.directory)
            assertEquals(RelatedMangaReason.SHARED_FOLDER_TAG, match.reason)
        }
    }

    @Test
    fun skipsBracketedReleaseCodesAndFormatFlags() = runBlocking {
        val source = File("[C104] [English Digital] Quiet Skies [1080p] [Vol. 2] [Full Color]")
        val other = candidate("[C104] [English Digital] Paper Lantern [1080p] [Vol. 2] [Full Color]")

        assertTrue(rankRelatedManga(source, null, listOf(other)).isEmpty())
    }

    @Test
    fun usesCoverMatchesToFillSlotsWithoutTagsOrSimilarTitles() = runBlocking {
        val similarTitle = candidate("Sample Manga 14")
        val similarCover = candidate("A Different Name")
        val unrelated = candidate("Unrelated Story")
        val matches = rankRelatedManga(
            File("Sample Manga 13"), null, listOf(unrelated, similarCover, similarTitle)
        ) { mapOf(similarCover.cover to 4) }

        assertEquals(
            listOf(RelatedMangaReason.SIMILAR_TITLE, RelatedMangaReason.SIMILAR_COVER),
            matches.map { it.reason }
        )
    }

    @Test
    fun coverMatchesOutrankAndReplaceFolderTagMatches() = runBlocking {
        val source = File("[Moon Library] Quiet Skies")
        val folderMatch = candidate("[Moon Library] Paper Lantern")
        val imageAndFolderMatch = candidate("[Moon Library] Hidden Station")
        val imageMatch = candidate("Marble Orchard")
        val matches = rankRelatedManga(source, null, listOf(folderMatch, imageAndFolderMatch, imageMatch)) {
            mapOf(imageMatch.cover to 0, imageAndFolderMatch.cover to 3)
        }

        assertEquals(listOf(imageMatch.directory, imageAndFolderMatch.directory, folderMatch.directory),
            matches.map { it.directory })
        assertEquals(listOf(RelatedMangaReason.SIMILAR_COVER, RelatedMangaReason.SIMILAR_COVER,
            RelatedMangaReason.SHARED_FOLDER_TAG), matches.map { it.reason })
    }

    @Test
    fun coverFingerprintToleratesBrightnessChangesAndRejectsBlankImages() {
        val original = IntArray(81) { if ((it / 9 + it % 9) % 2 == 0) 50 else 185 }
        val lighter = IntArray(81) { original[it] + 30 }
        val unrelated = IntArray(81) { 255 - original[it] }
        val fingerprint = requireNotNull(fingerprintCover(original))

        assertEquals(0, fingerprint.distanceTo(requireNotNull(fingerprintCover(lighter))))
        assertEquals(null, fingerprint.distanceTo(requireNotNull(fingerprintCover(unrelated))))
        assertEquals(null, fingerprintCover(IntArray(81) { 100 }))
    }

    @Test
    fun excludesTheCurrentMangaAndLimitsTiesToSix() = runBlocking {
        val source = candidate("Moon Garden", listOf(series))
        val candidates = (8 downTo 1).map { candidate("Volume $it", listOf(series)) } + source

        val matches = rankRelatedManga(source.directory, source.metadata, candidates)
        assertEquals((1..6).map { "Volume $it" }, matches.map { it.title })
    }

    @Test
    fun commonTagsAloneDoNotPopulateTheRow() = runBlocking {
        val candidates = (1..7).map { candidate("Folder $it", generalTags) }
        assertTrue(rankRelatedManga(File("Source"), metadata("Source", generalTags), candidates).isEmpty())
    }

    @Test
    fun discoversCoversOnceAcrossOverlappingRootsAndSkipsMissingFolders() = runBlocking {
        val root = temporaryFolder.newFolder("library")
        val manga = File(root, "A Manga").apply { mkdir() }
        val firstCover = File(manga, "1.JPG").apply { createNewFile() }
        File(manga, "02.jpg").createNewFile()
        File(root, "metadata.json").createNewFile()

        assertEquals(
            mapOf(manga.canonicalFile to firstCover.canonicalFile),
            discoverLibraryCovers(listOf(root, manga, File(root, "missing")))
        )
    }

    private fun candidate(name: String, tags: List<Tag> = emptyList()) = RelatedMangaCandidate(
        directory = File(name),
        cover = File(name, "1.jpg"),
        metadata = if (tags.isEmpty()) null else metadata(name, tags)
    )

    private fun metadata(name: String, tags: List<Tag>, japaneseTitle: String = "") = DoujinDetailsWithTags(
        DoujinDetails.getEmptyObject(File(name)).copy(fullTitleJapanese = japaneseTitle),
        tags
    )
}
