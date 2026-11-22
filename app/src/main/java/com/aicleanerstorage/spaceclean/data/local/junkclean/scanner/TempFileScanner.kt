package com.aicleanerstorage.spaceclean.data.local.junkclean.scanner

import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.data.local.storage.ScanDirectoryHelper
import java.io.File

internal class TempFileScanner : FileSystemJunkScanner() {
    override val category: JunkCategory = JunkCategory.TEMP_FILE

    override fun getRootDirectories(): List<File> = ScanDirectoryHelper.commonPublicDirectories()

    override fun isJunkFile(file: File): Boolean {
        if (!file.isFile || file.length() < MIN_TEMP_BYTES) return false
        val name = file.name.lowercase()
        val extension = file.extension.lowercase()
        return extension in TEMP_EXTENSIONS ||
            name.contains(".tmp") ||
            name.contains(".temp") ||
            name.startsWith("~") ||
            name.startsWith("temp_") ||
            name.startsWith("tmp_")
    }

    private companion object {
        const val MIN_TEMP_BYTES = 1024L
        val TEMP_EXTENSIONS = setOf("tmp", "temp", "log", "cache", "bak", "backup")
    }
}
