package com.aicleanerstorage.spaceclean.domain.model

data class ManagedFileItemModel(
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
    val duplicatePeers: List<ManagedFileItemModel> = emptyList(),
)
