package com.aicleanerstorage.spaceclean.data.repository

import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.aicleanerstorage.spaceclean.data.local.DirectoryFileScanner
import com.aicleanerstorage.spaceclean.data.local.DuplicateFileSelection
import com.aicleanerstorage.spaceclean.data.local.FileContentHasher
import com.aicleanerstorage.spaceclean.data.local.FileLocationMetadata
import com.aicleanerstorage.spaceclean.data.local.ManagedFileClassifier
import com.aicleanerstorage.spaceclean.data.local.MediaStoreFileQuery
import com.aicleanerstorage.spaceclean.data.local.toAndroidUri
import com.aicleanerstorage.spaceclean.domain.model.FileDeleteResultModel
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileItemModel
import com.aicleanerstorage.spaceclean.domain.model.ManagedFileType
import com.aicleanerstorage.spaceclean.domain.port.FileRepository
import java.io.File
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal class FileRepositoryImpl(context: Context) : FileRepository {
    private val context = context.applicationContext

    override suspend fun loadImages() =
        withContext(Dispatchers.IO) {
            MediaStoreFileQuery.queryMedia(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ManagedFileType.Image,
                coroutineContext,
            )
        }

    override suspend fun loadVideos() =
        withContext(Dispatchers.IO) {
            MediaStoreFileQuery.queryMedia(
                context.contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                ManagedFileType.Video,
                coroutineContext,
            )
        }

    override suspend fun loadAudios() =
        withContext(Dispatchers.IO) {
            MediaStoreFileQuery.queryMedia(
                context.contentResolver,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                ManagedFileType.Audio,
                coroutineContext,
            )
        }

    override suspend fun loadScreenshots() =
        withContext(Dispatchers.IO) {
            loadImages().filter {
                it.bucketName?.contains("screenshot", true) == true ||
                    it.path?.contains("screenshot", true) == true
            }
        }

    override suspend fun loadPrivacyImages() =
        withContext(Dispatchers.IO) {
            loadImages().filter { item ->
                coroutineContext.ensureActive()
                FileLocationMetadata.hasGpsLocation(context, item)
            }
        }

    override suspend fun loadDocuments() =
        withContext(Dispatchers.IO) {
            (queryFiles(context) + discoverDocumentFilesFromFileSystemIfAllowed(coroutineContext::ensureActive))
                .filter { item -> ManagedFileClassifier.isDocumentFile(item.name, item.mimeType) && !ManagedFileClassifier.isMediaFile(item.name, item.mimeType) }
                .distinctBy(::fileIdentity)
                .sortedByDescending { it.modifiedSeconds }
        }

    override suspend fun loadLargeFiles(minBytes: Long) =
        withContext(Dispatchers.IO) {
            discoverFiles(context)
                .filter { it.sizeBytes >= minBytes }
                .sortedByDescending { it.sizeBytes }
        }

    override suspend fun loadDuplicateFiles(): List<List<ManagedFileItemModel>> =
        withContext(Dispatchers.IO) {
            val files = discoverFiles(context).filter { it.type != ManagedFileType.Image }
            DuplicateFileSelection.buildDuplicateFileGroups(files) { item ->
                FileContentHasher.hash(context, item, coroutineContext)
            }
        }

    override suspend fun loadWhatsAppFiles() =
        withContext(Dispatchers.IO) {
            runCatching {
                (queryFiles(context).filter(::isWhatsAppCandidate) +
                    DirectoryFileScanner.scanWhatsAppFiles(coroutineContext::ensureActive))
                    .filter { it.sizeBytes > 0L }
                    .distinctBy(::fileIdentity)
                    .sortedByDescending { it.modifiedSeconds }
            }.getOrElse {
                coroutineContext.ensureActive()
                emptyList()
            }
        }

    override suspend fun deleteFiles(items: List<ManagedFileItemModel>): FileDeleteResultModel =
        withContext(Dispatchers.IO) {
            var freed = 0L
            var deletedCount = 0
            var skippedCount = 0
            val selectedIdentities = items.map(::fileIdentity).toSet()
            items.distinctBy(::fileIdentity).forEach { item ->
                coroutineContext.ensureActive()
                val duplicate = item.duplicateHash != null || item.duplicatePeers.isNotEmpty()
                if (duplicate && !DuplicateFileSelection.canDeleteDuplicate(item, selectedIdentities) {
                        FileContentHasher.hash(context, it, coroutineContext)
                    }
                ) {
                    skippedCount++
                    return@forEach
                }
                coroutineContext.ensureActive()
                val deleted =
                    if (duplicate && !item.path.isNullOrBlank() && File(item.path).isFile) {
                        runCatching { File(item.path).delete() }.getOrDefault(false)
                    } else {
                        runCatching { context.contentResolver.delete(item.uri.toAndroidUri(), null, null) > 0 }
                            .getOrDefault(false)
                    }
                val deletedFromPath =
                    !duplicate && !deleted && item.path != null && runCatching { File(item.path).delete() }.getOrDefault(false)
                if (deleted || deletedFromPath || (!duplicate && !fileStillExists(context, item))) {
                    freed += item.sizeBytes.coerceAtLeast(0L)
                    deletedCount++
                } else {
                    skippedCount++
                }
            }
            FileDeleteResultModel(freedBytes = freed, deletedCount = deletedCount, skippedCount = skippedCount)
        }

    private fun hasAllFilesAccess(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
            runCatching { Environment.isExternalStorageManager() }.getOrDefault(false)

    private fun fileIdentity(item: ManagedFileItemModel): String =
        ManagedFileClassifier.identity(item.path, item.uri.value)

    override suspend fun removeLocationData(items: List<ManagedFileItemModel>) = FileLocationMetadata.removeLocationData(context, items)

    private suspend fun discoverFiles(context: Context): List<ManagedFileItemModel> {
        val coroutineContext = currentCoroutineContext()
        return (
            queryFiles(context) +
                discoverPublicFilesFromFileSystemIfAllowed(coroutineContext::ensureActive)
        ).distinctBy(::fileIdentity)
    }

    private fun discoverPublicFilesFromFileSystemIfAllowed(checkCancellation: () -> Unit): List<ManagedFileItemModel> =
        if (hasAllFilesAccess()) {
            runCatching { DirectoryFileScanner.scanPublicFiles(checkCancellation) }.getOrElse {
                checkCancellation()
                emptyList()
            }
        } else {
            emptyList()
        }

    private fun discoverDocumentFilesFromFileSystemIfAllowed(checkCancellation: () -> Unit): List<ManagedFileItemModel> =
        if (hasAllFilesAccess()) {
            runCatching { DirectoryFileScanner.scanDocumentFiles(checkCancellation) }.getOrElse {
                checkCancellation()
                emptyList()
            }
        } else {
            emptyList()
        }

    private suspend fun queryFiles(context: Context): List<ManagedFileItemModel> =
        MediaStoreFileQuery.queryFiles(context.contentResolver, currentCoroutineContext())

    private fun fileStillExists(context: Context, item: ManagedFileItemModel): Boolean {
        item.path?.takeIf { it.isNotBlank() }?.let { if (File(it).exists()) return true }
        return runCatching {
            context.contentResolver.openFileDescriptor(item.uri.toAndroidUri(), "r")?.use { true } ?: false
        }.getOrDefault(false)
    }

    private fun isWhatsAppCandidate(item: ManagedFileItemModel): Boolean {
        val source = listOfNotNull(item.path, item.bucketName, item.name).joinToString("/").lowercase(Locale.US)
        return WHATSAPP_PATH_MARKERS.any { it in source }
    }

    private val WHATSAPP_PATH_MARKERS =
        listOf(
            "/whatsapp/", "/whatsapp business/", "/gbwhatsapp/", "/yowhatsapp/", "/fmwhatsapp/",
            "com.whatsapp", "com.whatsapp.w4b",
        )
}
