package com.aicleanerstorage.spaceclean.app.view.common.permission

import com.aicleanerstorage.spaceclean.base.permission.AppPermission

/** 文件权限流程中需要上报的业务事实。 */
internal sealed interface PermissionPromptEvent {
    data class DialogResult(
        val accepted: Boolean,
    ) : PermissionPromptEvent

    data class PermissionResult(
        val permission: AppPermission,
        val accepted: Boolean,
    ) : PermissionPromptEvent
}
