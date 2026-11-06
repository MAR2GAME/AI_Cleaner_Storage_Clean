package com.aicleanerstorage.spaceclean.feature.files.intent

import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem
enum class FileManagerMediaType {
    Photos,
    SimilarPhotos,
    PhotoPrivacy,
    Screenshots,
    Videos,
    Audios,
    LargeFiles,
    Documents,
    DuplicateFiles,
}

sealed interface FileManagerOperation {
    data class Load(val mediaType: FileManagerMediaType) : FileManagerOperation
    data class Delete(val files: List<ManagedFileItem>) : FileManagerOperation
}
