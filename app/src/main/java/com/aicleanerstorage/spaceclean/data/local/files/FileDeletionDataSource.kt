package com.aicleanerstorage.spaceclean.data.local.files

import android.app.RecoverableSecurityException
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.aicleanerstorage.spaceclean.base.permission.MediaDeletionRequester
import com.aicleanerstorage.spaceclean.data.local.storage.MediaDeletionAuthorization
import com.aicleanerstorage.spaceclean.data.local.storage.PendingMediaDelete
import com.aicleanerstorage.spaceclean.data.local.storage.authorizePendingMedia
import com.aicleanerstorage.spaceclean.data.local.storage.inspectFile
import com.aicleanerstorage.spaceclean.data.local.storage.inspectMedia
import com.aicleanerstorage.spaceclean.data.local.storage.unlinkFile
import com.aicleanerstorage.spaceclean.domain.model.files.DeleteStatus
import com.aicleanerstorage.spaceclean.domain.model.files.DuplicateFileSelectionModel
import com.aicleanerstorage.spaceclean.domain.model.files.ManagedFileModel
import com.aicleanerstorage.spaceclean.domain.rule.DuplicateFileSelectionRule
import com.aicleanerstorage.spaceclean.domain.rule.FileClassificationRule
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * 文件删除的平台数据源。
 *
 * 负责删除前的状态复核、重复文件保留校验、MediaStore 删除与系统授权登记；
 * 不负责页面状态、删除动画或失败文案。repository 只调用 [delete] 并把
 * [FileDeleteResult] 汇总成 domain 的 `FileDeleteSummaryModel`。
 *
 * @param context 用于访问 ContentResolver 的上下文。
 * @param requester 系统删除授权契约；为 null 时改为登记待授权请求。
 * @param authorization 待授权请求的登记表，页面退出后由它去重和收口。
 */
internal class FileDeletionDataSource(
    context: Context,
    private val requester: MediaDeletionRequester?,
    private val authorization: MediaDeletionAuthorization,
) {
    private val context = context.applicationContext
    private val deletionLock = Mutex()

    /**
     * 删除选中的文件。
     *
     * 同一时刻只执行一批删除；每个文件先复核大小/修改时间，重复文件再确认
     * 还有未选中的相同副本，避免删掉最后一份。
     *
     * @param files 待删除文件；内部按文件身份去重。
     * @return 逐文件结果；部分失败通过每个 [FileDeleteOutcome.status] 表达。
     */
    suspend fun delete(files: List<ManagedFileModel>): FileDeleteResult =
        withContext(Dispatchers.IO) {
            deletionLock.withLock { deleteLocked(files) }
        }

    /**
     * 删除重复文件页选中的条目。
     *
     * 每个条目都携带所属组，删除前用组哈希和未选中副本做安全校验。
     *
     * @param selections 选中的文件及其组。
     * @return 逐文件结果；部分失败通过每个 [FileDeleteOutcome.status] 表达。
     */
    suspend fun deleteDuplicates(selections: List<DuplicateFileSelectionModel>): FileDeleteResult =
        withContext(Dispatchers.IO) {
            deletionLock.withLock {
                deleteLocked(
                    items = selections.map { it.file },
                    duplicateSelections = selections.associateBy { fileIdentity(it.file) },
                )
            }
        }

    private suspend fun deleteLocked(
        items: List<ManagedFileModel>,
        duplicateSelections: Map<String, DuplicateFileSelectionModel> = emptyMap(),
    ): FileDeleteResult {
        val coroutineContext = currentCoroutineContext()
        val resolver = context.contentResolver
        val selection = items.distinctBy(::fileIdentity)
        val selectedIds = selection.map(::fileIdentity).toSet()
        val pending = mutableMapOf<ManagedFileModel, PendingMediaDelete>()
        val outcomes =
            selection.map { item ->
                coroutineContext.ensureActive()
                authorization.invalidate(MediaDeletionAuthorization.Owner.Files, fileIdentity(item))
                val uri = item.uri.toAndroidUri()
                val media = uri.scheme == "content" && uri.authority == MediaStore.AUTHORITY
                val path = item.path?.let(::File)
                val expected = PendingMediaDelete(uri, item.sizeBytes, item.modifiedSeconds, null)
                val initial =
                    when {
                        media -> inspectMedia(resolver, expected)
                        uri.scheme == "file" && path != null && uri.path == path.path ->
                            inspectFile(path, item.sizeBytes, item.modifiedSeconds, seconds = true)
                        else -> DeleteStatus.Unknown
                    }
                if (initial != null) return@map FileDeleteOutcome(item, initial)
                val duplicateSelection = duplicateSelections[fileIdentity(item)]
                val duplicate = duplicateSelection != null
                if (duplicateSelection != null &&
                    !DuplicateFileSelectionRule.canDelete(duplicateSelection, selectedIds) {
                        FileContentHasher.hash(context, it, coroutineContext)
                    }
                ) {
                    return@map FileDeleteOutcome(item, DeleteStatus.Changed)
                }
                coroutineContext.ensureActive()
                val recheck =
                    if (media) {
                        inspectMedia(resolver, expected)
                    } else {
                        path?.let { inspectFile(it, item.sizeBytes, item.modifiedSeconds, seconds = true) }
                    }
                if (recheck != null) return@map FileDeleteOutcome(item, recheck)
                try {
                    val deleted = if (media) resolver.delete(uri, null, null) > 0 else path?.let(::unlinkFile) == true
                    FileDeleteOutcome(item, if (deleted) DeleteStatus.Deleted else DeleteStatus.Failed)
                } catch (error: SecurityException) {
                    // Deferred deletion would lose the duplicate's retained-copy check.
                    if (!media ||
                        duplicate ||
                        (error !is RecoverableSecurityException && Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                    ) {
                        FileDeleteOutcome(item, DeleteStatus.Failed)
                    } else {
                        val request =
                            expected.copy(
                                recovery = (error as? RecoverableSecurityException)?.userAction?.actionIntent?.intentSender,
                            )
                        if (requester != null) {
                            pending[item] = request
                            FileDeleteOutcome(item, DeleteStatus.AuthorizationRequired)
                        } else {
                            val id =
                                authorization.register(
                                    MediaDeletionAuthorization.Owner.Files,
                                    fileIdentity(item),
                                    request,
                                )
                            FileDeleteOutcome(item, DeleteStatus.AuthorizationRequired, id)
                        }
                    }
                } catch (_: Exception) {
                    FileDeleteOutcome(item, DeleteStatus.Unknown)
                }
            }
        val confirmed =
            requester?.let { active ->
                authorizePendingMedia(resolver, pending.values.toList(), active::request)
            }.orEmpty()
        val final =
            outcomes.map { outcome ->
                val request = pending[outcome.item]
                if (request == null) outcome else outcome.copy(status = confirmed[request.uri] ?: DeleteStatus.Failed)
            }
        return FileDeleteResult(final)
    }

    private fun fileIdentity(item: ManagedFileModel): String =
        FileClassificationRule.identity(item.path, item.uri)
}
