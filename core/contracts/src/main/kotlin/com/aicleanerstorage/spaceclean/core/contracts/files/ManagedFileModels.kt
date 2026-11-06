package com.aicleanerstorage.spaceclean.core.contracts.files

import com.aicleanerstorage.spaceclean.core.contracts.format.FileSizeFormatter

enum class ManagedFileType { Image, Video, Audio, Document, Other }

@JvmInline
value class FileUri(val value: String) {
    override fun toString(): String = value
}

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
) {
    val formattedSize: String get() = FileSizeFormatter.format(sizeBytes)
}
