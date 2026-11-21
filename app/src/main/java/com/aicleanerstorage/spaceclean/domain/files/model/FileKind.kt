package com.aicleanerstorage.spaceclean.domain.files.model

/** Kind of a managed file, independent from the storage medium that produced it. */
enum class FileKind {
    Image,
    Video,
    Audio,
    Document,
    Other,
}
