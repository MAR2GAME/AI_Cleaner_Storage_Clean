package com.aicleanerstorage.spaceclean.feature.junkclean.scanner

import com.aicleanerstorage.spaceclean.feature.junkclean.model.JunkCategory
import com.aicleanerstorage.spaceclean.feature.junkclean.model.JunkFile

interface JunkScanner {
    val category: JunkCategory

    suspend fun scan(): List<JunkFile>

    fun getProgress(): Float
}
