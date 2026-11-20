package com.aicleanerstorage.cleanerkit.files.model

data class ManagedFileItem(
    val id: Long,
    val uri: FileUri,
    val path: String?,
    val name: String,
    val sizeBytes: Long,
    val modifiedSeconds: Long,
    val mimeType: String?,
    val bucketName: String?,
    val type: ManagedFileType,
    val duplicateHash: String? = null,
    val duplicatePeers: List<ManagedFileItem> = emptyList(),
)
