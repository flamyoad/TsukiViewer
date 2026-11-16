# Related manga in TsukiViewer

## User-visible behavior

The classic XML manga screen shows a **Related manga** section below **Tags** and above **Folder Details** on its **Details** tab. The switch is under **Settings → General → Show related manga**. It defaults to on, and an explicit off setting is retained. The section shows a loading message, an empty result message, or a horizontal list of covers with a reason on each card.

Six results are shown initially. Scrolling to within two cards of the end requests six more. The existing items keep their scroll position as new cards are appended. Tapping a card opens its Details tab, and Back returns to the preceding manga. On a wide screen, the row can request the next batch as soon as all current cards fit in the viewport.

## Where the code runs

- The classic entry point is `DoujinDetailsActivity`. It calls `DoujinViewModel.scanForImages` with the manga folder before setting up the pager. This sets the related-manga query's source even when the **Image Grid** is the landing tab.
- `DoujinViewModel.relatedMangaPages` is an eagerly shared state flow. The query starts when it receives a folder, uses the ViewModel's coroutine scope, and retains the results and requested item count across tab changes. Closing the manga screen cancels unfinished work for that screen.
- `FragmentDoujinDetails` collects the state while its tab is resumed. The collector only renders state. It does not start a separate recommendation job. `RelatedMangaAdapter` uses a horizontal RecyclerView with recycled cover cards, and the fragment requests more when scrolling approaches the end.
- `RelatedMangaRepository` lives in `core`. It first observes the preference, then performs the enabled search on the I/O dispatcher. A disabled preference produces a hidden state before any filesystem scan, recommendation cache query, or cover decode. Turning the feature off cancels the outstanding search at coroutine cancellation points.
- There is also an existing Compose details surface. Its `observe` path uses the same ranker and cover fingerprint cache but returns a single batch. The eager ViewModel, paged UI, and persisted ranked-list lookup described here are the classic XML path.

## How matches are ranked

Candidates are local folders containing supported, readable image files under the configured **Included folders**. The search canonicalizes paths, deduplicates overlapping roots, and excludes the open manga. It picks the first readable page using the same length-then-name sort used by the basic page ordering. The feature needs no server and works for folders with no imported metadata.

Match reasons are ordered as follows. Each card shows its strongest qualifying reason.

1. Same series and character
2. Same series
3. Shares a character
4. Similar title
5. **Similar cover**
6. **Shared folder tag**
7. Same artist
8. Shares tags

Title comparisons use normalized English, Japanese, and folder names. They remove common leading or trailing release markers and volume suffixes, and compute similarity both by shared words and by adjacent character pairs.

**Shared folder tag** uses meaningful bracketed names even when the visible titles differ. For example, `[Moon Library] Quiet Skies` and `Paper Lantern [Moon Library]` can match. Full-width brackets and prefixes such as `[Artist: Mika]` are normalized. A bracketed name can match a name in another folder or relevant saved metadata. Common language, format, and release flags such as `[English]`, `[Digital]`, and `[C104]` are ignored.

**Similar cover** uses near-duplicate artwork. The app samples a small grayscale grid and records two 64-bit fingerprints based on horizontal and vertical brightness changes. A combined Hamming distance of at most 12 qualifies. Blank or low-detail images are skipped. This can match resized or slightly brightened copies of the same art, but it is not an art-style or character recognition model.

The ranker first prepares matches by series, character, and title. If these fill the requested page, it publishes them and defers cover comparison until more results are requested. Otherwise, the prepared matches can appear while covers are being checked. A verified cover match takes precedence over a folder tag for the same item, and cover matches also sort ahead of folder-only matches in the list. Saved series, saved characters, and similar titles still rank above cover matches.

## Caches and invalidation

| Layer | Contents | Lifetime |
| --- | --- | --- |
| ViewModel | Current result state and requested item count | While that manga screen exists |
| In-memory library cache | Shared folder scan, prepared ranks, and recently resolved results | Up to five minutes per generation, with a bounded per-title cache |
| Room `cover_fingerprints` | One row per canonical manga folder, with cover path, file size, modification time, algorithm version, and two nullable hashes | Survives app restarts. Refreshes during cover comparison when the selected cover changes |
| Room `related_manga_cache` | One row per source folder with ordered matches, cover validation data, ranking/library key, timestamp, and a flag for pending cover search | Survives app restarts. Entries expire after 24 hours |

The ranked cache can store a partial result while cover comparison is deferred, as well as a complete result, including an empty match list. Disk hits verify the library key and the recorded file stamps for the source and matched covers. Expired records and records with a mismatched key are pruned when the enabled feature opens its disk cache. Invalid or corrupt result records are deleted when encountered, then rebuilt as needed. Metadata changes, included-folder changes, and changes detected by an in-app library rescan invalidate the in-memory generation. Deletion of the corresponding disk cache is deferred to the next enabled lookup so the feature still performs no recommendation I/O while off.

The library key includes the included paths and their modification times, the title and tag metadata used for ranking, and a ranking version in `SavedRelatedManga.kt`. Bump this version when changing ranking semantics. Fingerprint algorithm versioning is separate in `CoverFingerprints.kt`. External changes deep inside a folder may only be detected by a rescan or after cache expiry when they leave the stored paths and file stamps unchanged.

The cold path is slower because it has to walk the included folders. If a page needs image matches, it also has to decode each eligible cover missing a current fingerprint. A ranked cache hit avoids the directory walk and ranking. A fingerprint hit avoids image decoding during a cover search.

## Room migrations

The active database lives in the `core` module. Its schema version is **7**. Migration **5 → 6** adds `cover_fingerprints`. Migration **6 → 7** adds `related_manga_cache`. Both are additive and keep the existing library, metadata, bookmarks, recent tabs, and other app data. The cache tables key by folder path so they also work when no metadata row exists.

Generated Room schemas are in `core/schemas/com.flamyoad.tsukiviewer.core.db.AppDatabase/`. The app's Android migration tests load those and the historical version 3 and 4 schemas in `app/schemas/`.

## Verification

```sh
./gradlew :core:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest
./gradlew :app:connectedDebugAndroidTest
```

- `RelatedMangaTest` covers ranking, numbered titles, bracketed names, ignored release flags, covers outranking folder tags, and deduplicated folder discovery.
- `CoverFingerprintCacheTest` exercises real image decoding, persistence, changed/corrupt/deleted covers, loading and empty states, deferred cover comparison, zero Room queries with the switch off, and persistent result reuse and stale-entry deletion.
- `RoomMigrationTest` validates upgrades from versions 3, 4, 5, and 6 against the current exported schema, including preservation of version 6 cover fingerprints.
- To smoke-test in the XML UI, add two unrelated names with the same non-blank cover and another manga that shares only a bracketed tag with the source. Set **Landing screen** to **Image Grid**. Open the source and verify it is in Images. The source's ranked cache entry should appear before the Details tab is opened. In Details, the matching artwork should say **Similar cover** and appear before the **Shared folder tag** match. For pagination, use a title with more than six results and scroll the carousel near the end.
