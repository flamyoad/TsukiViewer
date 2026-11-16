package com.flamyoad.tsukiviewer.core.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.flamyoad.tsukiviewer.core.model.CoverFingerprintEntry

@Dao
interface CoverFingerprintDao {
    @Query("SELECT * FROM cover_fingerprints")
    suspend fun getAll(): List<CoverFingerprintEntry>

    @Upsert
    suspend fun upsert(entries: List<CoverFingerprintEntry>)
}
