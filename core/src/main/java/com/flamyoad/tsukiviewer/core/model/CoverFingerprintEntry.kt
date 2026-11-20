package com.flamyoad.tsukiviewer.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cover_fingerprints")
data class CoverFingerprintEntry(
    @PrimaryKey val directoryPath: String,
    val coverPath: String,
    val lastModified: Long,
    val byteCount: Long,
    val algorithmVersion: Int,
    val horizontalHash: Long?,
    val verticalHash: Long?,
    val paletteHash: Long? = null,
    val characterFingerprint: ByteArray? = null
)
