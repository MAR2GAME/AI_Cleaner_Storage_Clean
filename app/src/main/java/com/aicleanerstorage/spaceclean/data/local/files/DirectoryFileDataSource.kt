package com.aicleanerstorage.spaceclean.data.local.files

import android.content.Context
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.requireGranted
import com.aicleanerstorage.spaceclean.data.local.storage.DirectoryFileScanner
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.rule.FileClassificationRule
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

/**
 * 指定目录下的文件查询数据源。
 *
 * 合并 MediaStore 中已有记录和文件系统扫描结果，按文件身份去重；
 * 供 WhatsApp 等只关心固定目录的业务使用，不对外暴露目录细节。
 *
 * @param context 用于访问 MediaStore 和外部存储。
 */
internal class DirectoryFileDataSource(
    private val context: Context,
) {
    /**
     * 查询指定相对目录下的文件。
     *
     * @param relativeDirectories 外部存储下的相对目录；不允许绝对路径或 `..`。
     * @param matchesMedia 只保留满足条件的 MediaStore 记录。
     * @return 去重并按修改时间倒序的文件；目录为空且无权限时抛出权限异常。
     */
    suspend fun filesInDirectories(
        relativeDirectories: List<String>,
        matchesMedia: (ManagedFileModel) -> Boolean,
    ): List<ManagedFileModel> =
        withContext(Dispatchers.IO) {
            require(
                relativeDirectories.all { path ->
                    path.isNotBlank() && !File(path).isAbsolute &&
                        path.split('/').none { it.isEmpty() || it == "." || it == ".." }
                },
            ) { "Expected relative directory paths below external storage" }
            runCatching {
                (
                    queryFiles().filter(matchesMedia) +
                        DirectoryFileScanner.scanDirectories(relativeDirectories, coroutineContext::ensureActive)
                ).filter { it.sizeBytes > 0L }
                    .distinctBy(::fileIdentity)
                    .sortedByDescending { it.modifiedSeconds }
                    .also { if (it.isEmpty()) AppPermission.StorageFiles.requireGranted(context) }
            }.getOrElse {
                if (it is SecurityException) throw it
                coroutineContext.ensureActive()
                emptyList()
            }
        }

    private suspend fun queryFiles(): List<ManagedFileModel> =
        MediaStoreFileQuery.queryFiles(context.contentResolver, currentCoroutineContext())

    private fun fileIdentity(item: ManagedFileModel): String =
        FileClassificationRule.identity(item.path, item.uri)
}
