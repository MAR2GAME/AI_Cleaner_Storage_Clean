package com.aicleanerstorage.cleanerkit.clean.model

data class JunkFile(
    val id: String,
    val filePath: String,
    val fileName: String,
    val fileSize: Long,
    val category: JunkCategory,
    val lastModified: Long = System.currentTimeMillis(),
    val duplicateHash: String? = null,
    val duplicateOriginalPath: String? = null,
    val scanId: String = "",
)
