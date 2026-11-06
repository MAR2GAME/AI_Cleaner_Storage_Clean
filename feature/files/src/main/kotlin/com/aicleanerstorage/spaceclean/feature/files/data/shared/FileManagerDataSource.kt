package com.aicleanerstorage.spaceclean.feature.files.data.shared

import android.content.Context
import android.media.ExifInterface
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.aicleanerstorage.spaceclean.core.contracts.files.DuplicateContentGuard
import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileItem
import com.aicleanerstorage.spaceclean.core.contracts.files.ManagedFileType
import com.aicleanerstorage.spaceclean.core.contracts.files.FileDeleteResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

object FileManagerDataSource {
    suspend fun loadImages(context: Context) =
        withContext(Dispatchers.IO) {
            MediaStoreFileQuery.queryMedia(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ManagedFileType.Image,
                coroutineContext,
            )
        }

    suspend fun loadVideos(context: Context) =
        withContext(Dispatchers.IO) {
            MediaStoreFileQuery.queryMedia(
                context.contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                ManagedFileType.Video,
                coroutineContext,
            )
        }

    suspend fun loadAudios(context: Context) =
        withContext(Dispatchers.IO) {
            MediaStoreFileQuery.queryMedia(
                context.contentResolver,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                ManagedFileType.Audio,
                coroutineContext,
            )
        }

    suspend fun loadScreenshots(context: Context) =
        withContext(Dispatchers.IO) {
            loadImages(context).filter {
                it.bucketName?.contains("screenshot", true) == true ||
                    it.path?.contains("screenshot", true) == true
            }
        }

    suspend fun loadPrivacyImages(context: Context) =
        withContext(Dispatchers.IO) {
            loadImages(context).filter { item ->
                coroutineContext.ensureActive()
                hasGpsLocation(context, item)
            }
        }

    suspend fun loadDocuments(context: Context) =
        withContext(Dispatchers.IO) {
            (queryFiles(context) + discoverDocumentFilesFromFileSystemIfAllowed(coroutineContext::ensureActive))
                .filter { item -> isAllowedDocumentCandidate(item.name, item.mimeType) }
                .distinctBy(::fileIdentity)
                .sortedByDescending { it.modifiedSeconds }
        }

    suspend fun loadLargeFiles(context: Context, minBytes: Long = 10L * 1024 * 1024) =
        withContext(Dispatchers.IO) {
            discoverFiles(context)
                .filter { it.sizeBytes >= minBytes }
                .sortedByDescending { it.sizeBytes }
        }

    suspend fun loadDuplicateFiles(context: Context): List<List<ManagedFileItem>> =
        withContext(Dispatchers.IO) {
            val files = discoverFiles(context).filter { it.type != ManagedFileType.Image }
            buildDuplicateFileGroups(files) { item ->
                FileContentHasher.hash(context, item, coroutineContext)
            }
        }

    suspend fun loadWhatsAppFiles(context: Context) =
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

    suspend fun deleteFiles(context: Context, items: List<ManagedFileItem>): FileDeleteResult =
        withContext(Dispatchers.IO) {
            var freed = 0L
            var deletedCount = 0
            var skippedCount = 0
            val selectedIdentities = items.map(::fileIdentity).toSet()
            items.distinctBy(::fileIdentity).forEach { item ->
                coroutineContext.ensureActive()
                val duplicate = item.duplicateHash != null || item.duplicatePeers.isNotEmpty()
                if (duplicate && !canDeleteDuplicate(item, selectedIdentities) {
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
            FileDeleteResult(freedBytes = freed, deletedCount = deletedCount, skippedCount = skippedCount)
        }

    suspend fun removeLocationData(context: Context, items: List<ManagedFileItem>): Int =
        withContext(Dispatchers.IO) {
            var changed = 0
            items.forEach { item ->
                coroutineContext.ensureActive()
                val path = item.path
                if (!path.isNullOrBlank()) {
                    val removed =
                        runCatching {
                            val exif = ExifInterface(path)
                            clearGpsAttributes(exif)
                            exif.saveAttributes()
                            true
                        }.getOrDefault(false)
                    if (removed) changed++
                }
            }
            changed
        }

    internal fun canDeleteDuplicate(
        item: ManagedFileItem,
        selectedIdentities: Set<String>,
        contentHash: (ManagedFileItem) -> String?,
    ): Boolean = DuplicateContentGuard.hasVerifiedCopy(
        candidate = item,
        retainedCandidates = item.duplicatePeers.filter { fileIdentity(it) !in selectedIdentities },
        expectedHash = item.duplicateHash,
        identity = ::fileIdentity,
        contentHash = contentHash,
    )

    fun isDocumentFile(name: String, mimeType: String?): Boolean =
        ManagedFileClassifier.isDocumentFile(name, mimeType)

    fun isMediaFileName(name: String, mimeType: String?): Boolean =
        ManagedFileClassifier.isMediaFile(name, mimeType)

    fun isAllowedDocumentCandidate(name: String, mimeType: String?): Boolean =
        isDocumentFile(name, mimeType) && !isMediaFileName(name, mimeType)

    fun hasAllFilesAccess(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
            runCatching { Environment.isExternalStorageManager() }.getOrDefault(false)

    fun buildDuplicateFileGroups(
        files: List<ManagedFileItem>,
        contentHash: (ManagedFileItem) -> String?,
    ): List<List<ManagedFileItem>> {
        val hashes = mutableMapOf<String, String?>()
        return DuplicateFileGrouping.group(
            candidates = files.map(::duplicateCandidate),
            contentHash = { item -> contentHash(item).also { hashes[fileIdentity(item)] = it } },
        ).map { group ->
            group.map { item ->
                item.copy(
                    duplicateHash = hashes.getValue(fileIdentity(item)),
                    duplicatePeers = group,
                )
            }
        }
    }

    fun scanPublicFilesFromFileSystem(): List<ManagedFileItem> = DirectoryFileScanner.scanPublicFiles()

    internal fun scanDocumentFilesFromFileSystem(): List<ManagedFileItem> = DirectoryFileScanner.scanDocumentFiles()

    internal fun scanWhatsAppFilesFromFileSystem(): List<ManagedFileItem> = DirectoryFileScanner.scanWhatsAppFiles()

    private suspend fun discoverFiles(context: Context): List<ManagedFileItem> {
        val coroutineContext = currentCoroutineContext()
        return (
            queryFiles(context) +
                discoverPublicFilesFromFileSystemIfAllowed(coroutineContext::ensureActive)
        ).distinctBy(::fileIdentity)
    }

    private fun discoverPublicFilesFromFileSystemIfAllowed(checkCancellation: () -> Unit): List<ManagedFileItem> =
        if (hasAllFilesAccess()) {
            runCatching { DirectoryFileScanner.scanPublicFiles(checkCancellation) }.getOrElse {
                checkCancellation()
                emptyList()
            }
        } else {
            emptyList()
        }

    private fun discoverDocumentFilesFromFileSystemIfAllowed(checkCancellation: () -> Unit): List<ManagedFileItem> =
        if (hasAllFilesAccess()) {
            runCatching { DirectoryFileScanner.scanDocumentFiles(checkCancellation) }.getOrElse {
                checkCancellation()
                emptyList()
            }
        } else {
            emptyList()
        }

    private suspend fun queryFiles(context: Context): List<ManagedFileItem> =
        MediaStoreFileQuery.queryFiles(context.contentResolver, currentCoroutineContext())

    private fun duplicateCandidate(item: ManagedFileItem): DuplicateCandidate<ManagedFileItem> =
        DuplicateCandidate(
            value = item,
            identity = fileIdentity(item),
            name = item.name,
            sizeBytes = item.sizeBytes,
            modifiedSeconds = item.modifiedSeconds,
        )

    private fun fileStillExists(context: Context, item: ManagedFileItem): Boolean {
        item.path?.takeIf { it.isNotBlank() }?.let { if (File(it).exists()) return true }
        return runCatching {
            context.contentResolver.openFileDescriptor(item.uri.toAndroidUri(), "r")?.use { true } ?: false
        }.getOrDefault(false)
    }

    private fun hasGpsLocation(context: Context, item: ManagedFileItem): Boolean =
        runCatching {
            val path = item.path
            if (!path.isNullOrBlank()) {
                ExifInterface(path).hasGpsLocation()
            } else {
                val itemUri = item.uri.toAndroidUri()
                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.setRequireOriginal(itemUri) else itemUri
                context.contentResolver.openInputStream(uri)?.use { ExifInterface(it).hasGpsLocation() } ?: false
            }
        }.getOrDefault(false)

    private fun ExifInterface.hasGpsLocation(): Boolean =
        FloatArray(2).let(::getLatLong) || getAttribute(ExifInterface.TAG_GPS_LATITUDE) != null

    private fun clearGpsAttributes(exif: ExifInterface) {
        listOf(
            ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_LATITUDE_REF,
            ExifInterface.TAG_GPS_LONGITUDE,
            ExifInterface.TAG_GPS_LONGITUDE_REF,
            ExifInterface.TAG_GPS_ALTITUDE,
            ExifInterface.TAG_GPS_ALTITUDE_REF,
            ExifInterface.TAG_GPS_DATESTAMP,
            ExifInterface.TAG_GPS_TIMESTAMP,
            ExifInterface.TAG_GPS_PROCESSING_METHOD,
        ).forEach { exif.setAttribute(it, null) }
    }

    private fun isWhatsAppCandidate(item: ManagedFileItem): Boolean {
        val source = listOfNotNull(item.path, item.bucketName, item.name).joinToString("/").lowercase(Locale.US)
        return WHATSAPP_PATH_MARKERS.any { it in source }
    }

    private fun fileIdentity(item: ManagedFileItem): String =
        ManagedFileClassifier.identity(item.path, item.uri.value)

    private val WHATSAPP_PATH_MARKERS =
        listOf(
            "/whatsapp/", "/whatsapp business/", "/gbwhatsapp/", "/yowhatsapp/", "/fmwhatsapp/",
            "com.whatsapp", "com.whatsapp.w4b",
        )
}
