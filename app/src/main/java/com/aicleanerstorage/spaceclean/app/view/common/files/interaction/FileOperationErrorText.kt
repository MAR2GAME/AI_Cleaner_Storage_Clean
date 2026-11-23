package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.permission.descriptionRes
import com.aicleanerstorage.spaceclean.base.mvi.UiText
import com.aicleanerstorage.spaceclean.base.permission.PermissionRequiredException
import com.aicleanerstorage.spaceclean.base.util.FormatUtil
import com.aicleanerstorage.spaceclean.domain.model.files.FileDeleteSummaryModel

/**
 * 把文件操作错误统一映射成 UiText。
 *
 * @return 可直接交给页面展示的文案；权限错误使用对应权限的说明。
 */
internal fun FileOperationError.toUiText(): UiText =
    when (this) {
        FileOperationError.ScanFailed -> UiText.Res(R.string.file_scan_failed)
        FileOperationError.DuplicateScanFailed -> UiText.Res(R.string.duplicate_scan_failed)
        FileOperationError.DeletionFailed -> UiText.Res(R.string.deletion_failed)
        is FileOperationError.PermissionRequired -> UiText.Res(permission.descriptionRes())
    }

/**
 * 把异常收口成可展示的文件操作错误。
 *
 * 权限缺失优先映射为 [FileOperationError.PermissionRequired]；
 * 其他异常使用调用方给的 [fallback]，避免每个 ViewModel 重复判断。
 *
 * @param fallback 非权限异常时使用的错误。
 * @return 可直接展示的文件操作错误。
 */
internal fun Throwable.toFileOperationError(fallback: FileOperationError): FileOperationError =
    if (this is PermissionRequiredException) {
        FileOperationError.PermissionRequired(permission)
    } else {
        fallback
    }

internal fun FileDeleteSummaryModel.retryMessage(): UiText? =
    when {
        isComplete -> null
        isAuthorizationCancelled ->
            UiText.Res(R.string.cleanup_stopped_summary, listOf(deletedCount, FormatUtil.formatSize(freedBytes)))
        processedCount == 0 -> FileOperationError.DeletionFailed.toUiText()
        else -> UiText.Res(R.string.clean_result_files_failed, listOf(skippedCount))
    }
