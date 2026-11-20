package com.aicleanerstorage.spaceclean.domain.port

import com.aicleanerstorage.spaceclean.domain.model.FileDeleteResultModel
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel

interface FileRepository {
    suspend fun loadImages(): List<ManagedFileItemModel>

    suspend fun loadVideos(): List<ManagedFileItemModel>

    suspend fun loadAudios(): List<ManagedFileItemModel>

    suspend fun loadScreenshots(): List<ManagedFileItemModel>

    suspend fun loadPrivacyImages(): List<ManagedFileItemModel>

    suspend fun loadDocuments(): List<ManagedFileItemModel>

    suspend fun loadLargeFiles(minBytes: Long = 10L * 1024 * 1024): List<ManagedFileItemModel>

    suspend fun loadDuplicateFiles(): List<List<ManagedFileItemModel>>

    suspend fun loadWhatsAppFiles(): List<ManagedFileItemModel>

    suspend fun deleteFiles(items: List<ManagedFileItemModel>): FileDeleteResultModel

    suspend fun removeLocationData(items: List<ManagedFileItemModel>): Int
}
