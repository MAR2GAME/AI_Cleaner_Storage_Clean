package com.aicleanerstorage.cleanerkit.files.internal

import kotlinx.coroutines.sync.withLock

import kotlinx.coroutines.sync.Mutex

import com.aicleanerstorage.cleanerkit.MediaDeletionAuthorization

import android.app.RecoverableSecurityException
import android.content.IntentSender
import com.aicleanerstorage.cleanerkit.files.model.DeleteStatus
import com.aicleanerstorage.cleanerkit.files.model.FileDeleteOutcome
import com.aicleanerstorage.cleanerkit.internal.storage.PendingMediaDelete
import com.aicleanerstorage.cleanerkit.internal.storage.inspectMedia
import com.aicleanerstorage.cleanerkit.internal.storage.inspectFile
import com.aicleanerstorage.cleanerkit.internal.storage.unlinkFile
import com.aicleanerstorage.cleanerkit.internal.storage.authorizePendingMedia
import android.Manifest
import android.content.pm.PackageManager
import com.aicleanerstorage.cleanerkit.files.FileAccessRequiredException
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.aicleanerstorage.cleanerkit.files.FileRepository
import com.aicleanerstorage.cleanerkit.files.model.FileDeleteResult
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileItem
import com.aicleanerstorage.cleanerkit.files.model.ManagedFileType
import com.aicleanerstorage.cleanerkit.internal.storage.DirectoryFileScanner
import com.aicleanerstorage.cleanerkit.internal.storage.ManagedFileClassifier
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

internal class FileRepositoryImpl(
    context: Context,
    private val authorize: (suspend (IntentSender) -> Boolean)?,
    private val authorization: MediaDeletionAuthorization,
) : FileRepository {
    private val context = context.applicationContext
    private val deletionLock = Mutex()

    override suspend fun loadImages() =
        withContext(Dispatchers.IO) {
            MediaStoreFileQuery.queryMedia(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ManagedFileType.Image,
                coroutineContext,
            ).also { if (it.isEmpty()) requireMediaReadAccess(Manifest.permission.READ_MEDIA_IMAGES) }
        }

    override suspend fun loadVideos() =
        withContext(Dispatchers.IO) {
            MediaStoreFileQuery.queryMedia(
                context.contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                ManagedFileType.Video,
                coroutineContext,
            ).also { if (it.isEmpty()) requireMediaReadAccess(Manifest.permission.READ_MEDIA_VIDEO) }
        }

    override suspend fun loadAudios() =
        withContext(Dispatchers.IO) {
            MediaStoreFileQuery.queryMedia(
                context.contentResolver,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                ManagedFileType.Audio,
                coroutineContext,
            ).also { if (it.isEmpty()) requireMediaReadAccess(Manifest.permission.READ_MEDIA_AUDIO) }
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
            if (!granted(Manifest.permission.ACCESS_MEDIA_LOCATION)) throw FileAccessRequiredException(Manifest.permission.ACCESS_MEDIA_LOCATION)
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
                .sortedByDescending { it.modifiedSeconds }.also { if (it.isEmpty()) requireSharedStorageAccess() }
        }

    override suspend fun loadLargeFiles(minBytes: Long) =
        withContext(Dispatchers.IO) {
            discoverFiles(context)
                .filter { it.sizeBytes >= minBytes }
                .sortedByDescending { it.sizeBytes }
        }

    override suspend fun loadDuplicateFiles(): List<List<ManagedFileItem>> =
        withContext(Dispatchers.IO) {
            val files = discoverFiles(context).filter { it.type != ManagedFileType.Image }
            DuplicateFileSelection.buildDuplicateFileGroups(files) { item ->
                FileContentHasher.hash(context, item, coroutineContext)
            }
        }

    override suspend fun loadFilesInDirectories(
        relativeDirectories: List<String>,
        matchesMedia: (ManagedFileItem) -> Boolean,
    ) =
        withContext(Dispatchers.IO) {
            require(relativeDirectories.all { path ->
                path.isNotBlank() && !File(path).isAbsolute &&
                    path.split('/').none { it.isEmpty() || it == "." || it == ".." }
            }) { "Expected relative directory paths below external storage" }
            runCatching {
                (queryFiles(context).filter(matchesMedia) +
                    DirectoryFileScanner.scanDirectories(relativeDirectories, coroutineContext::ensureActive))
                    .filter { it.sizeBytes > 0L }
                    .distinctBy(::fileIdentity)
                    .sortedByDescending { it.modifiedSeconds }.also { if (it.isEmpty()) requireSharedStorageAccess() }
            }.getOrElse {
                if (it is SecurityException) throw it
                coroutineContext.ensureActive()
                emptyList()
            }
        }

    override suspend fun deleteFiles(items: List<ManagedFileItem>): FileDeleteResult =
        withContext(Dispatchers.IO) {
            deletionLock.withLock { deleteSelection(items) }
        }

    private suspend fun deleteSelection(items: List<ManagedFileItem>): FileDeleteResult {
        val coroutineContext = currentCoroutineContext()
        val resolver = context.contentResolver
        val selection = items.distinctBy(::fileIdentity)
        val selectedIds = selection.map(::fileIdentity).toSet()
        val pending = mutableMapOf<ManagedFileItem, PendingMediaDelete>()
        val outcomes = selection.map { item ->
            coroutineContext.ensureActive()
            authorization.invalidate(MediaDeletionAuthorization.Owner.Files, fileIdentity(item))
            val uri = item.uri.toAndroidUri()
            val media = uri.scheme == "content" && uri.authority == MediaStore.AUTHORITY
            val path = item.path?.let(::File)
            val expected = PendingMediaDelete(uri, item.sizeBytes, item.modifiedSeconds, null)
            val initial = when {
                media -> inspectMedia(resolver, expected)
                uri.scheme == "file" && path != null && uri.path == path.path ->
                    inspectFile(path, item.sizeBytes, item.modifiedSeconds, seconds = true)
                else -> DeleteStatus.Unknown
            }
            if (initial != null) return@map FileDeleteOutcome(item, initial)
            val duplicate = item.duplicateHash != null || item.duplicatePeers.isNotEmpty()
            if (duplicate && !DuplicateFileSelection.canDeleteDuplicate(item, selectedIds) {
                    FileContentHasher.hash(context, it, coroutineContext)
                }
            ) return@map FileDeleteOutcome(item, DeleteStatus.Changed)
            coroutineContext.ensureActive()
            val recheck = if (media) inspectMedia(resolver, expected) else path?.let {
                inspectFile(it, item.sizeBytes, item.modifiedSeconds, seconds = true)
            }
            if (recheck != null) return@map FileDeleteOutcome(item, recheck)
            try {
                val deleted = if (media) resolver.delete(uri, null, null) > 0 else path?.let(::unlinkFile) == true
                FileDeleteOutcome(item, if (deleted) DeleteStatus.Deleted else DeleteStatus.Failed)
            } catch (error: SecurityException) {
                // Deferred deletion would lose the duplicate's retained-copy check.
                if (!media || duplicate || (error !is RecoverableSecurityException && Build.VERSION.SDK_INT < Build.VERSION_CODES.R)) {
                    FileDeleteOutcome(item, DeleteStatus.Failed)
                } else {
                    val request = expected.copy(recovery = (error as? RecoverableSecurityException)?.userAction?.actionIntent?.intentSender)
                    if (authorize != null) {
                        pending[item] = request
                        FileDeleteOutcome(item, DeleteStatus.AuthorizationRequired)
                    } else {
                        val id = authorization.register(MediaDeletionAuthorization.Owner.Files, fileIdentity(item), request)
                        FileDeleteOutcome(item, DeleteStatus.AuthorizationRequired, id)
                    }
                }
            } catch (_: Exception) {
                FileDeleteOutcome(item, DeleteStatus.Unknown)
            }
        }
        val confirmed = authorize?.let { authorizePendingMedia(resolver, pending.values.toList(), it) }.orEmpty()
        val final = outcomes.map { outcome ->
            val request = pending[outcome.item]
            if (request == null) outcome else outcome.copy(status = confirmed[request.uri] ?: DeleteStatus.Failed)
        }
        return FileDeleteResult(final)
    }

    private fun granted(permission: String) =
        context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    private fun requireMediaReadAccess(mediaPermission: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) return
        val permission = if (Build.VERSION.SDK_INT >= 33) mediaPermission else Manifest.permission.READ_EXTERNAL_STORAGE
        if (!granted(permission)) throw FileAccessRequiredException(permission)
    }

    private fun requireSharedStorageAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) throw FileAccessRequiredException(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        } else if (!granted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            throw FileAccessRequiredException(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun hasAllFilesAccess(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
            runCatching { Environment.isExternalStorageManager() }.getOrDefault(false)

    private fun fileIdentity(item: ManagedFileItem): String =
        ManagedFileClassifier.identity(item.path, item.uri.value)

    override suspend fun removeLocationData(items: List<ManagedFileItem>) = FileLocationMetadata.removeLocationData(context, items)

    private suspend fun discoverFiles(context: Context): List<ManagedFileItem> {
        val coroutineContext = currentCoroutineContext()
        return (
            queryFiles(context) +
                discoverPublicFilesFromFileSystemIfAllowed(coroutineContext::ensureActive)
        ).distinctBy(::fileIdentity).also { if (it.isEmpty()) requireSharedStorageAccess() }
    }

    private fun discoverPublicFilesFromFileSystemIfAllowed(checkCancellation: () -> Unit): List<ManagedFileItem> =
        if (hasAllFilesAccess()) {
            runCatching { DirectoryFileScanner.scanPublicFiles(checkCancellation) }.getOrElse {
                if (it is SecurityException) throw it
                checkCancellation()
                emptyList()
            }
        } else {
            emptyList()
        }

    private fun discoverDocumentFilesFromFileSystemIfAllowed(checkCancellation: () -> Unit): List<ManagedFileItem> =
        if (hasAllFilesAccess()) {
            runCatching { DirectoryFileScanner.scanDocumentFiles(checkCancellation) }.getOrElse {
                if (it is SecurityException) throw it
                checkCancellation()
                emptyList()
            }
        } else {
            emptyList()
        }

    private suspend fun queryFiles(context: Context): List<ManagedFileItem> =
        MediaStoreFileQuery.queryFiles(context.contentResolver, currentCoroutineContext())

}
