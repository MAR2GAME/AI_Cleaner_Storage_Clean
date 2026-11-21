package com.aicleanerstorage.spaceclean.data.files

import com.aicleanerstorage.cleanerkit.files.model.FileUri
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileType
import com.aicleanerstorage.spaceclean.domain.files.model.FileKind
import com.aicleanerstorage.spaceclean.domain.files.model.ManagedFileModel

internal fun ManagedFileItem.toModel(): ManagedFileModel =
    ManagedFileModel(
        id = id,
        uri = uri.value,
        path = path,
        name = name,
        sizeBytes = sizeBytes,
        modifiedSeconds = modifiedSeconds,
        mimeType = mimeType,
        bucketName = bucketName,
        kind = type.toFileKind(),
    )

internal fun ManagedFileModel.toItem(): ManagedFileItem =
    ManagedFileItem(
        id = id,
        uri = FileUri(uri),
        path = path,
        name = name,
        sizeBytes = sizeBytes,
        modifiedSeconds = modifiedSeconds,
        mimeType = mimeType,
        bucketName = bucketName,
        type = kind.toManagedFileType(),
    )

private fun ManagedFileType.toFileKind(): FileKind =
    when (this) {
        ManagedFileType.Image -> FileKind.Image
        ManagedFileType.Video -> FileKind.Video
        ManagedFileType.Audio -> FileKind.Audio
        ManagedFileType.Document -> FileKind.Document
        ManagedFileType.Other -> FileKind.Other
    }

private fun FileKind.toManagedFileType(): ManagedFileType =
    when (this) {
        FileKind.Image -> ManagedFileType.Image
        FileKind.Video -> ManagedFileType.Video
        FileKind.Audio -> ManagedFileType.Audio
        FileKind.Document -> ManagedFileType.Document
        FileKind.Other -> ManagedFileType.Other
    }
