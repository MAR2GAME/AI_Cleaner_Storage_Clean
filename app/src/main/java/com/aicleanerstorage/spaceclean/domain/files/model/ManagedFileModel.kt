package com.aicleanerstorage.spaceclean.domain.files.model

/** A file surfaced by a cleanup page, detached from the storage medium that produced it. */
data class ManagedFileModel(
    val id: Long,
    val uri: String,
    val path: String?,
    val name: String,
    val sizeBytes: Long,
    val modifiedSeconds: Long,
    val mimeType: String?,
    val bucketName: String?,
    val kind: FileKind,
)
