package com.aicleanerstorage.spaceclean.data.repository

import android.content.Context
import android.provider.MediaStore
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.requireGranted
import com.aicleanerstorage.spaceclean.data.catching
import com.aicleanerstorage.spaceclean.data.local.files.FileContentHasher
import com.aicleanerstorage.spaceclean.data.local.files.FileDeletionDataSource
import com.aicleanerstorage.spaceclean.data.local.files.FileLocationMetadata
import com.aicleanerstorage.spaceclean.data.local.files.MediaStoreFileQuery
import com.aicleanerstorage.spaceclean.data.local.files.toSummary
import com.aicleanerstorage.spaceclean.data.local.storage.DirectoryFileScanner
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileGroupModel
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileSelectionModel
import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel
import com.aicleanerstorage.spaceclean.domain.model.files.FileKind
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.port.ManagedFileRepository
import com.aicleanerstorage.spaceclean.domain.rule.DuplicateFileSelectionRule
import com.aicleanerstorage.spaceclean.domain.rule.FileClassificationRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/**
 * 文件查询、目录扫描与位置数据清理的唯一实现，直接实现 [ManagedFileRepository]。
 *
 * 查询留在本类；删除交给 [FileDeletionDataSource]；权限判断统一委托给
 * [AppPermission]：普通读取在结果为空时断言 [AppPermission.StorageFiles]，
 * 隐私图额外断言 [AppPermission.MediaLocation]。方法签名和返回语义见端口 KDoc。
 *
 * @param context 用于访问 MediaStore、文件系统和权限的上下文。
 * @param deletion 删除数据源；负责状态复核、重复文件保留校验和系统授权。
 */
internal class DefaultManagedFileRepository(
    context: Context,
    private val deletion: FileDeletionDataSource,
) : ManagedFileRepository {
    private val context = context.applicationContext

    override suspend fun loadImages(): List<ManagedFileModel> =
        queryImages().also { if (it.isEmpty()) AppPermission.StorageFiles.requireGranted(context) }

    override suspend fun loadVideos(): List<ManagedFileModel> =
        queryVideos().also { if (it.isEmpty()) AppPermission.StorageFiles.requireGranted(context) }

    override suspend fun loadAudios(): List<ManagedFileModel> =
        queryAudios().also { if (it.isEmpty()) AppPermission.StorageFiles.requireGranted(context) }

    override suspend fun loadScreenshots(): List<ManagedFileModel> =
        queryImages()
            .also { if (it.isEmpty()) AppPermission.StorageFiles.requireGranted(context) }
            .filter {
                it.bucketName?.contains("screenshot", true) == true ||
                    it.path?.contains("screenshot", true) == true
            }

    /**
     * 读取带 GPS 的图片；缺少 [AppPermission.MediaLocation] 时抛出
     * [com.aicleanerstorage.spaceclean.base.permission.PermissionRequiredException]。
     *
     * @return 带位置信息的图片列表；没有位置权限时不会返回空列表，而是抛异常让页面申请权限。
     */
    override suspend fun loadPrivacyImages(): List<ManagedFileModel> =
        withContext(Dispatchers.IO) {
            AppPermission.MediaLocation.requireGranted(context)
            queryImages().filter { item ->
                coroutineContext.ensureActive()
                FileLocationMetadata.hasGpsLocation(context, item)
            }
        }

    override suspend fun loadDocuments(): List<ManagedFileModel> =
        withContext(Dispatchers.IO) {
            (queryFiles(context) + discoverDocumentFilesFromFileSystemIfAllowed(coroutineContext::ensureActive))
                .filter { item ->
                    FileClassificationRule.isDocumentFile(item.name, item.mimeType) &&
                        !FileClassificationRule.isMediaFile(item.name, item.mimeType)
                }.distinctBy(::fileIdentity)
                .sortedByDescending { it.modifiedSeconds }
                .also { if (it.isEmpty()) AppPermission.StorageFiles.requireGranted(context) }
        }

    override suspend fun loadLargeFiles(minBytes: Long): List<ManagedFileModel> =
        withContext(Dispatchers.IO) {
            discoverFiles(context)
                .filter { it.sizeBytes >= minBytes }
                .sortedByDescending { it.sizeBytes }
        }

    override suspend fun loadDuplicateGroups(): List<DuplicateFileGroupModel> =
        withContext(Dispatchers.IO) {
            val files = discoverFiles(context).filter { it.kind != FileKind.Image }
            DuplicateFileSelectionRule.buildGroups(files) { item ->
                FileContentHasher.hash(context, item, coroutineContext)
            }
        }

    override suspend fun delete(files: List<ManagedFileModel>): Result<FileDeleteSummaryModel> =
        catching {
            deletion.delete(files).toSummary()
        }

    override suspend fun deleteDuplicates(selections: List<DuplicateFileSelectionModel>): Result<FileDeleteSummaryModel> =
        catching {
            deletion.deleteDuplicates(selections).toSummary()
        }

    override suspend fun removeLocationData(files: List<ManagedFileModel>): Result<Int> =
        catching {
            FileLocationMetadata.removeLocationData(context, files)
        }

    private suspend fun queryImages(): List<ManagedFileModel> =
        withContext(Dispatchers.IO) {
            MediaStoreFileQuery.queryMedia(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                FileKind.Image,
                coroutineContext,
            )
        }

    private suspend fun queryVideos(): List<ManagedFileModel> =
        withContext(Dispatchers.IO) {
            MediaStoreFileQuery.queryMedia(
                context.contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                FileKind.Video,
                coroutineContext,
            )
        }

    private suspend fun queryAudios(): List<ManagedFileModel> =
        withContext(Dispatchers.IO) {
            MediaStoreFileQuery.queryMedia(
                context.contentResolver,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                FileKind.Audio,
                coroutineContext,
            )
        }

    private fun fileIdentity(item: ManagedFileModel): String = FileClassificationRule.identity(item.path, item.uri)

    private suspend fun discoverFiles(context: Context): List<ManagedFileModel> {
        val coroutineContext = currentCoroutineContext()
        return (
            queryFiles(context) +
                discoverPublicFilesFromFileSystemIfAllowed(coroutineContext::ensureActive)
        ).distinctBy(::fileIdentity).also { if (it.isEmpty()) AppPermission.StorageFiles.requireGranted(context) }
    }

    private fun discoverPublicFilesFromFileSystemIfAllowed(checkCancellation: () -> Unit): List<ManagedFileModel> =
        if (AppPermission.StorageFiles.isGranted(context)) {
            runCatching { DirectoryFileScanner.scanPublicFiles(checkCancellation) }.getOrElse {
                if (it is SecurityException) throw it
                checkCancellation()
                emptyList()
            }
        } else {
            emptyList()
        }

    private fun discoverDocumentFilesFromFileSystemIfAllowed(checkCancellation: () -> Unit): List<ManagedFileModel> =
        if (AppPermission.StorageFiles.isGranted(context)) {
            runCatching { DirectoryFileScanner.scanDocumentFiles(checkCancellation) }.getOrElse {
                if (it is SecurityException) throw it
                checkCancellation()
                emptyList()
            }
        } else {
            emptyList()
        }

    private suspend fun queryFiles(context: Context): List<ManagedFileModel> =
        MediaStoreFileQuery.queryFiles(context.contentResolver, currentCoroutineContext())
}
