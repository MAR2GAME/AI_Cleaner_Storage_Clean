package com.aicleanerstorage.spaceclean.data.local.junkclean.scanner

import android.content.Context
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import java.io.File

internal class CacheScanner(
    private val context: Context,
) : FileSystemJunkScanner() {
    override val category = JunkCategory.CACHE

    override fun getRootDirectories(): List<File> = listOf(context.cacheDir) + context.externalCacheDirs.filterNotNull()

    override fun isJunkFile(file: File): Boolean = true
}
