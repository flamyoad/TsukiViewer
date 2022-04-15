package com.flamyoad.tsukiviewer.repository

import com.flamyoad.tsukiviewer.db.dao.IncludedPathDao
import java.io.File
import javax.inject.Inject


interface DirectoryManager {
    suspend fun getIncludedDirectories(): List<File>
}

class DirectoryManagerImpl @Inject constructor(
    private val includedPathDao: IncludedPathDao
) : DirectoryManager {

    override suspend fun getIncludedDirectories(): List<File> {
        return includedPathDao.getAllBlocking()
    }

}