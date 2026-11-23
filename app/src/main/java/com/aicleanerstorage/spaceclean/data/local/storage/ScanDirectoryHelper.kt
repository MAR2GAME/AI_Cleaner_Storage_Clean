package com.aicleanerstorage.spaceclean.data.local.storage

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File

internal object ScanDirectoryHelper {
    fun junkDirectories(context: Context): List<File> {
        val external = externalStorageDirectoryOrNull()
        val directories =
            buildList {
                add(context.filesDir)
                addAll(context.externalMediaDirs.filterNotNull())
                addAll(context.getExternalFilesDirs(null).filterNotNull())
                if (external != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
                    add(external)
                } else {
                    addAll(commonPublicDirectories())
                    if (external != null) addAll(listOf(File(external, "APK"), File(external, "apk")))
                }
            }
        return directories
            .filter { it.isReadableDirectory() }
            .distinct()
            .sortedBy { it.path.length }
            .fold(mutableListOf()) { roots, directory ->
                if (roots.none { directory.toPath().startsWith(it.toPath()) }) roots.add(directory)
                roots
            }
    }

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
