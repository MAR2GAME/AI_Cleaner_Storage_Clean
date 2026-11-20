package com.aicleanerstorage.cleanerkit.clean.internal.scanner

import com.aicleanerstorage.cleanerkit.clean.model.JunkCategory
import com.aicleanerstorage.cleanerkit.clean.model.JunkFile

internal interface JunkScanner {
    val category: JunkCategory

    suspend fun scan(): List<JunkFile>
}
