package com.aicleanerstorage.spaceclean.data.local

import com.aicleanerstorage.spaceclean.domain.model.JunkCategory
import com.aicleanerstorage.spaceclean.domain.model.JunkFileModel

interface JunkScanner {
    val category: JunkCategory

    suspend fun scan(): List<JunkFileModel>

    fun getProgress(): Float
}
