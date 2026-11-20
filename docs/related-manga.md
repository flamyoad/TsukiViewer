# Related manga in TsukiViewer

## User-visible behavior

The classic XML manga screen shows a **Related manga** section below **Tags** and above **Folder Details** on its **Details** tab. The switch is under **Settings → General → Show related manga**. It defaults to on, and an explicit off setting is retained. The section shows a loading message, an empty result message, or a horizontal list of covers with a reason on each card.

Six results are shown initially. Scrolling to within two cards of the end requests six more. The existing items keep their scroll position as new cards are appended. Tapping a card opens its Details tab, and Back returns to the preceding manga. On a wide screen, the row can request the next batch as soon as all current cards fit in the viewport.

## Where the code runs

- The classic entry point is `DoujinDetailsActivity`. It calls `DoujinViewModel.scanForImages` with the manga folder before setting up the pager. This records the folder for the query when the **Details** tab becomes visible.
- `DoujinViewModel.relatedMangaPages` runs while the **Details** tab is collecting. Leaving the tab or screen cancels the scan between covers. The ViewModel retains the requested item count, and the repository reuses completed cache entries when the tab reopens.
- `FragmentDoujinDetails` collects the state while its tab is resumed. `RelatedMangaAdapter` uses a horizontal RecyclerView with recycled cover cards, and the fragment requests more when scrolling approaches the end.
- `RelatedMangaRepository` lives in `core`. It first observes the preference, then performs the enabled search on the I/O dispatcher. A disabled preference produces a hidden state before any filesystem scan, recommendation cache query, or cover decode. Turning the feature off cancels the outstanding search at coroutine cancellation points.
- The Compose overview uses the same paged repository while **Details** is visible and displays up to six results.

## How matches are ranked

Candidates are local folders containing supported, readable image files under the configured **Included folders**. The search canonicalizes paths, deduplicates overlapping roots, and excludes the open manga. It picks the first readable page using the same length-then-name sort used by the basic page ordering. The feature needs no server and works for folders with no imported metadata.

Match reasons are ordered as follows. Each card shows its strongest qualifying reason.

1. Same series and character
2. Same series
3. Shares a character
4. Similar title
5. **Similar cover**
6. **Similar character art**
7. **Shared folder tag**
8. Same artist
9. Shares tags

Title comparisons use normalized English, Japanese, and folder names. They remove common leading or trailing release markers and volume suffixes, and compute similarity both by shared words and by adjacent character pairs.

**Shared folder tag** uses meaningful bracketed names even when the visible titles differ. For example, `[Moon Library] Quiet Skies` and `Paper Lantern [Moon Library]` can match. Full-width brackets and prefixes such as `[Artist: Mika]` are normalized. A bracketed name can match a name in another folder or relevant saved metadata. Common language, format, and release flags such as `[English]`, `[Digital]`, and `[C104]` are ignored.

**Similar cover** prioritizes near-duplicate artwork using two luminance hashes from a 9×9 sample and a compact hue histogram. Broader palette matches are available when an embedding is missing. When both embeddings are available, a weak palette match must pass character comparison.

**Similar character art** compares embeddings from CCIP, a model trained on anime character illustrations. It uses the selected cover on each folder. The score is half the cosine distance between the two embeddings, so lower scores indicate closer artwork. The app accepts distances at or below **0.16** and ranks those results by distance. The model works best with one clearly visible character. Group scenes, small faces, and changes in drawing style can be ambiguous. The metadata reason **Shares a character** still comes from saved character tags. [CCIP model card](https://huggingface.co/deepghs/ccip)

The ranker first prepares matches by series, character, and title. If these fill the requested page, it defers cover comparison until more results are requested. Otherwise, it publishes the available metadata matches while the cover scan runs and adds visual matches as batches finish.

## Android inference

- ONNX Runtime uses one CPU thread on the repository's I/O dispatcher. The memory-mapped asset is loaded only when an uncached cover needs an embedding. The session closes at the end of the scan or on cancellation.
- The packaged model takes a 160×160 RGB input. It uses 4-bit weights for most matrix multiplications and 8-bit weights for convolution weights. Each stored embedding is 768 signed bytes.
- The scanner skips low-detail placeholders, checks cancellation between covers, and writes completed fingerprints to Room every eight changes. Android 6 uses the cover hashes and palette. Character inference runs on Android 7 and newer.
- The asset is derived from DeepGHS's `ccip-caformer-24-randaug-pruned` model. The input was reduced from 384 to 160 pixels, the attention-pooling position grid was resized bicubically, and the weights were quantized for Android. The publisher identifies the model as OpenRAIL. [Pinned upstream model](https://huggingface.co/deepghs/ccip_onnx/tree/4236450/ccip-caformer-24-randaug-pruned)

## Caches and invalidation

| Layer | Contents | Lifetime |
| --- | --- | --- |
| ViewModel | Current result state and requested item count | While that manga screen exists |
| In-memory library cache | Shared folder scan, prepared ranks, and recently resolved results | Up to five minutes per generation, with a bounded per-title cache |
| Room `cover_fingerprints` | One row per canonical manga folder, with cover path, file size, modification time, algorithm version, two nullable edge hashes, a nullable palette signature, and a nullable character embedding | Survives app restarts. Refreshes during cover comparison when the selected cover changes |
| Room `related_manga_cache` | One row per source folder with ordered matches, cover validation data, ranking/library key, timestamp, and a flag for pending cover search | Survives app restarts. Entries expire after 24 hours |

The ranked cache can store a partial result while cover comparison is deferred, as well as a complete result, including an empty match list. Disk hits verify the library key and the recorded file stamps for the source and matched covers. Expired records and records with a mismatched key are pruned when the enabled feature opens its disk cache. Invalid or corrupt result records are deleted when encountered, then rebuilt as needed. Metadata changes, included-folder changes, and changes detected by an in-app library rescan invalidate the in-memory generation. Deletion of the corresponding disk cache is deferred to the next enabled lookup so the feature still performs no recommendation I/O while off.

The library key includes the included paths and their modification times, the title and tag metadata used for ranking, and a ranking version in `SavedRelatedManga.kt`. Bump this version when changing ranking semantics. Fingerprint algorithm versioning is separate in `CoverFingerprints.kt`. Character matching uses fingerprint algorithm version **3** and ranking version **4**. Old fingerprints refresh on demand, and old ranked results are pruned on the next enabled lookup. External changes deep inside a folder may only be detected by a rescan or after cache expiry when they leave the stored paths and file stamps unchanged.

The cold path is slower because it has to walk the included folders. If a page needs image matches, it decodes eligible covers missing a current fingerprint and runs inference on those with enough image detail. A ranked cache hit avoids the directory walk and ranking. A fingerprint hit avoids image decoding during a cover search.

## Room migrations

The active database lives in the `core` module. Its schema version is **10**. Migration **5 → 6** adds `cover_fingerprints`. Migration **6 → 7** adds `related_manga_cache`. Migration **7 → 8** adds the nullable `paletteHash` column. Migration **8 → 9** updates the recent-tab primary key. Migration **9 → 10** adds the nullable `characterFingerprint` column and preserves the existing cover hashes. The cache tables key by folder path so they also work when no metadata row exists.

Generated Room schemas are in `core/schemas/com.flamyoad.tsukiviewer.core.db.AppDatabase/`. The app's Android migration tests load those and the historical version 3 and 4 schemas in `app/schemas/`. The test asset merge depends on the core Room schema export so a build that introduces a new schema packages it on the first run.

## Verification

```sh
./gradlew :core:testDebugUnitTest :app:assembleDebug :app:assembleDebugAndroidTest
./gradlew :app:connectedDebugAndroidTest
```

- `RelatedMangaTest` verifies that character similarity can match different artwork and veto a palette-only match.
- `CoverFingerprintCacheTest` covers changed, corrupt, and deleted covers, deferred matching, zero Room queries with the switch off, and persisted result reuse.
- `RoomMigrationTest` verifies library data and covers survive database upgrades.
- `CharacterCoverBenchmarkTest` exercises the shipping Android model and repository using nine public reference images. It skips when those files are absent from the debug app's cache.

### Character benchmark

On the ARM64 Android 16 emulator, the first repository query across nine reference covers took **862 ms**. Repeating the query with cached fingerprints took **4 ms**. This measures the end-to-end repository query in that emulator run.

| Reference pair | Character distance |
| --- | ---: |
| Hatsune Miku, six combinations across four illustrations | 0.100–0.156 |
| Kagamine Rin and Kagamine Len | 0.179 |
| Hatsune Miku V4X and KAITO | 0.238 |
| Original Hatsune Miku and Megurine Luka | 0.325 |

All six same-character pairs and all 30 different-character pairs were on the expected side of the 0.16 cutoff in this reference set. This was a small, controlled reference set. [Crypton Future Media reference art](https://piapro.net/intl/en_character.html)

To rerun the benchmark, install the debug app and its instrumentation APK on the emulator, then stage the public images in the debug app's cache.

```sh
adb shell run-as com.flamyoad.tsukiviewer mkdir -p cache/ccip-benchmark
for image in ch_img_miku img_prod_mikuv4x img_prod_mikunt img_prod_mikuv2 ch_img_rin ch_img_len ch_img_luka ch_img_kaito ch_img_meiko; do
    curl -fLsS "https://piapro.net/intl/images/$image.png" |
        adb shell -T "run-as com.flamyoad.tsukiviewer sh -c 'cat > cache/ccip-benchmark/$image.png'"
done
adb shell am instrument -w \
    -e class com.flamyoad.tsukiviewer.CharacterCoverBenchmarkTest \
    com.flamyoad.tsukiviewer.test/androidx.test.runner.AndroidJUnitRunner
adb logcat -d -s CharacterCoverBenchmark:I
```
