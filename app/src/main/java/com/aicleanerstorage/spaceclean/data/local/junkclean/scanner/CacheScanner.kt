package com.aicleanerstorage.spaceclean.data.local.junkclean.scanner

import android.content.Context
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkSource
import java.io.File

internal class CacheScanner(
    private val context: Context,
) : FileSystemJunkScanner() {
    override val category = JunkCategory.CACHE

    override fun getRootDirectories(): List<File> = listOf(context.cacheDir) + context.externalCacheDirs.filterNotNull()

    override fun sourceOf(file: File): JunkSource = JunkSource.AppCache
}
