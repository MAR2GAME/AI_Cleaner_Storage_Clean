package com.aicleanerstorage.spaceclean.data.local.junkclean.scanner

import com.aicleanerstorage.spaceclean.domain.model.junk.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.junk.JunkFileModel

internal interface JunkScanner {
    val category: JunkCategory

    suspend fun scan(): List<JunkFileModel>
}
