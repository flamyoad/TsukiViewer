package com.flamyoad.tsukiviewer.core.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.widget.Toast
import com.flamyoad.tsukiviewer.core.R
import java.io.File
import java.io.IOException

fun openMangaFolder(context: Context, path: String?) {
    val folder = path?.takeIf(String::isNotBlank)?.let(::File)
    if (folder == null || !folder.isDirectory) {
        Toast.makeText(context, R.string.manga_folder_missing, Toast.LENGTH_SHORT).show()
        return
    }

    val uri = try {
        sharedStorageDocumentUri(context, folder.canonicalFile)
    } catch (_: IOException) {
        null
    }
    if (uri != null) {
        val viewFolder = Intent(Intent.ACTION_VIEW).setDataAndType(
            uri, DocumentsContract.Document.MIME_TYPE_DIR
        )
        if (context.tryStartFileBrowser(viewFolder)) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val browseFiles = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
            }
            if (context.tryStartFileBrowser(browseFiles)) return
        }
    }
    Toast.makeText(context, R.string.manga_folder_unavailable, Toast.LENGTH_SHORT).show()
}

@Suppress("DEPRECATION")
private fun sharedStorageDocumentUri(context: Context, folder: File): Uri? {
    val primaryRoot = Environment.getExternalStorageDirectory().canonicalFile
    val documentId = folder.relativeToOrNull(primaryRoot)?.toDocumentId("primary")
        ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val volume = context.getSystemService(StorageManager::class.java)?.getStorageVolume(folder)
            val uuid = volume?.uuid ?: return null
            val root = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                volume.directory ?: return null
            } else {
                File("/storage", uuid)
            }
            folder.relativeToOrNull(root.canonicalFile)?.toDocumentId(uuid)
        } else {
            null
        }
    return documentId?.let {
        DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", it)
    }
}

private fun File.toDocumentId(volumeId: String): String? {
    if (path == ".." || path.startsWith("../")) return null
    return "$volumeId:$path"
}

private fun Context.tryStartFileBrowser(intent: Intent): Boolean = try {
    startActivity(intent)
    true
} catch (_: ActivityNotFoundException) {
    false
} catch (_: SecurityException) {
    false
}
