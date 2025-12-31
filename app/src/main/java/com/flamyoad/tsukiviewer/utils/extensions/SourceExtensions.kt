package com.flamyoad.tsukiviewer.utils.extensions

import com.flamyoad.tsukiviewer.R
import com.flamyoad.tsukiviewer.core.model.Source

/**
 * Extension to get the drawable resource ID for a Source enum.
 * This keeps resource references in the app module while Source enum is in core.
 */
val Source.drawableId: Int
    get() = when (this) {
        Source.NHentai -> R.drawable.fav_nhentai_black
        // Add more sources as needed
    }
