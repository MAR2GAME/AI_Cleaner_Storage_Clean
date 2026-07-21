package com.aicleanerstorage.spaceclean.feature.files.shared.data

import com.aicleanerstorage.spaceclean.feature.files.shared.ManagedFileItem

data class FileDeleteResult(
    val freedBytes: Long,
    val deletedCount: Int,
)

interface FileRepository {
    suspend fun loadImages(): List<ManagedFileItem>

    suspend fun loadVideos(): List<ManagedFileItem>

    suspend fun loadAudios(): List<ManagedFileItem>

    suspend fun loadScreenshots(): List<ManagedFileItem>

    suspend fun loadPrivacyImages(): List<ManagedFileItem>

    suspend fun loadDocuments(): List<ManagedFileItem>

    suspend fun loadLargeFiles(minBytes: Long = 10L * 1024 * 1024): List<ManagedFileItem>

    suspend fun loadDuplicateFiles(): List<List<ManagedFileItem>>

    suspend fun loadWhatsAppFiles(): List<ManagedFileItem>

    suspend fun deleteFiles(items: List<ManagedFileItem>): FileDeleteResult

    suspend fun removeLocationData(items: List<ManagedFileItem>): Int

    fun hasAllFilesAccess(): Boolean
}
