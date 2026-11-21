package com.aicleanerstorage.cleanerkit.files

import com.aicleanerstorage.cleanerkit.files.model.FileDeleteResult
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem

interface FileRepository {
    suspend fun loadImages(): List<ManagedFileItem>

    suspend fun loadVideos(): List<ManagedFileItem>

    suspend fun loadAudios(): List<ManagedFileItem>

    suspend fun loadScreenshots(): List<ManagedFileItem>

    suspend fun loadPrivacyImages(): List<ManagedFileItem>

    suspend fun loadDocuments(): List<ManagedFileItem>

    suspend fun loadLargeFiles(minBytes: Long = 10L * 1024 * 1024): List<ManagedFileItem>

    suspend fun loadDuplicateFiles(): List<List<ManagedFileItem>>

    /** Combines matching MediaStore rows with files under the given external-storage subdirectories. */
    suspend fun loadFilesInDirectories(
        relativeDirectories: List<String>,
        matchesMedia: (ManagedFileItem) -> Boolean,
    ): List<ManagedFileItem>

    suspend fun deleteFiles(items: List<ManagedFileItem>): FileDeleteResult

    suspend fun removeLocationData(items: List<ManagedFileItem>): Int
}
