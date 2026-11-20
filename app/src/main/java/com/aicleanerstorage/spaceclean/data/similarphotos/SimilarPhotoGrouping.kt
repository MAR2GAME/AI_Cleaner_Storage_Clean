package com.aicleanerstorage.spaceclean.data.similarphotos

import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem

private data class PhotoBucket(val album: String, val day: Long, val sizeBucket: Long)

internal fun groupSimilarPhotos(files: List<ManagedFileItem>, limit: Int = 60): List<List<ManagedFileItem>> =
    files.take(limit)
        .groupBy { file ->
            PhotoBucket(
                album = file.bucketName.orEmpty().lowercase(),
                day = file.modifiedSeconds / 86_400L,
                sizeBucket = (file.sizeBytes / (512L * 1024L)).coerceAtLeast(0L),
            )
        }
        .values
        .filter { it.size > 1 }
        .take(12)
        .map { group -> group.sortedByDescending { it.sizeBytes } }
