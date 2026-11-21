package com.aicleanerstorage.cleanerkit.internal.storage

import android.os.Environment
import java.io.File

internal object ScanDirectoryHelper {
    fun commonPublicDirectories(): List<File> {
        val external = externalStorageDirectoryOrNull() ?: return emptyList()
        return listOf(
            File(external, Environment.DIRECTORY_DOWNLOADS),
            File(external, Environment.DIRECTORY_DOCUMENTS),
            File(external, Environment.DIRECTORY_PICTURES),
            File(external, Environment.DIRECTORY_DCIM),
            File(external, Environment.DIRECTORY_MOVIES),
            File(external, Environment.DIRECTORY_MUSIC),
            File(external, "Temp"),
            File(external, "temp"),
            File(external, "tmp"),
        ).filter { file ->
            runCatching { file.exists() && file.isDirectory && file.canRead() }.getOrDefault(false)
        }
    }

    fun externalStorageDirectoryOrNull(): File? = runCatching { Environment.getExternalStorageDirectory() }.getOrNull()

    fun File.isReadableDirectory(): Boolean = runCatching { exists() && isDirectory && canRead() }.getOrDefault(false)

}
