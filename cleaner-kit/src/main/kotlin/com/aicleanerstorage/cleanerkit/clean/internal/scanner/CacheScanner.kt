package com.aicleanerstorage.cleanerkit.clean.internal.scanner

import android.content.Context
import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory
import java.io.File

internal class CacheScanner(private val context: Context) : FileSystemJunkScanner() {
    override val category = JunkCategory.CACHE
    override fun getRootDirectories(): List<File> = listOf(context.cacheDir) + context.externalCacheDirs.filterNotNull()
    override fun isJunkFile(file: File): Boolean = true
}
