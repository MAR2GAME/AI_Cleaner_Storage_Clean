package com.aicleanerstorage.spaceclean.feature.junkclean.data.scanner

import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.feature.junkclean.domain.model.JunkFile

interface JunkScanner {
    val category: JunkCategory

    suspend fun scan(): List<JunkFile>

    fun getProgress(): Float
}
