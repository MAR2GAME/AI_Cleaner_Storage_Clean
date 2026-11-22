package com.aicleanerstorage.spaceclean.app.view.common.files.interaction

import com.aicleanerstorage.spaceclean.base.permission.AppPermission

/**
 * 文件管理家族的失败词汇。
 *
 * 只表达“扫描/删除/权限”三类可展示失败；到文案的映射统一收口在
 * [toUiText]，ViewModel 不再自己拼字符串。
 */
sealed interface FileOperationError {
    /** 普通列表/媒体扫描失败；页面回到无结果状态。 */
    data object ScanFailed : FileOperationError

    /** 重复文件扫描失败；页面保留重复文件页面的空态。 */
    data object DuplicateScanFailed : FileOperationError

    /** 删除失败；页面保留已选文件，允许重试。 */
    data object DeletionFailed : FileOperationError

    /**
     * 缺少读取权限。
     *
     * @property permission 缺失的权限；文案由 [toUiText] 按它选择说明。
     */
    data class PermissionRequired(
        val permission: AppPermission,
    ) : FileOperationError
}
