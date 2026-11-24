package com.aicleanerstorage.spaceclean.app.view.common.permission

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionGate
import com.aicleanerstorage.spaceclean.base.permission.PermissionResult

/**
 * 权限提示状态机：优先系统授权，系统拉不起时才解释。
 *
 * - 运行时权限：直接请求系统弹窗；
 * - 设置类权限：先解释，再跳设置；
 * - 运行时权限被永久拒绝：系统弹窗拉不起，改为解释后跳设置。
 *
 * @param gate 权限查询与系统界面入口。
 * @param requiresSettings 该权限是否只能通过设置页授予。
 * @param onEvent 文件权限流程的业务事实回调；由 Route 接线到埋点。
 */
@Stable
internal class PermissionPromptController(
    private val gate: PermissionGate,
    private val requiresSettings: (AppPermission) -> Boolean = { false },
    private val onEvent: (PermissionPromptEvent) -> Unit = {},
) {
    /** 正在展示解释弹窗的权限；null 表示没有。 */
    var explaining by mutableStateOf<AppPermission?>(null)
        private set

    /** 已进入系统界面的权限；null 表示没有。 */
    var requesting by mutableStateOf<AppPermission?>(null)
        private set

    /** 本次系统请求是否强制只跳设置页。 */
    var requestingSettingsOnly by mutableStateOf(false)
        private set

    private var callback: ((PermissionResult) -> Unit)? = null
    private var explainBeforeSettings = false
    private var activePermission: AppPermission? = null

    /**
     * 发起一次权限请求。
     *
     * 已授权时直接回调 [onResult]；运行时权限直接拉系统弹窗，设置类权限先解释。
     *
     * @param permission 目标权限。
     * @param onResult 最终结果回调。
     */
    fun request(
        permission: AppPermission,
        onResult: (PermissionResult) -> Unit,
    ) {
        if (explaining != null || requesting != null) return
        if (gate.isGranted(permission)) {
            onResult(PermissionResult.Granted)
            return
        }
        activePermission = permission
        callback = onResult
        if (requiresSettings(permission)) {
            explainBeforeSettings = true
            explaining = permission
        } else {
            requestingSettingsOnly = false
            requesting = permission
        }
    }

    /** 用户确认解释弹窗，进入系统界面。 */
    fun confirmExplanation() {
        val permission = explaining ?: return
        trackDialogResult(accepted = true)
        explaining = null
        requestingSettingsOnly = explainBeforeSettings || requiresSettings(permission)
        requesting = permission
    }

    /** 用户取消解释弹窗。 */
    fun dismissExplanation() {
        trackDialogResult(accepted = false)
        finish(PermissionResult.Denied)
    }

    /**
     * 系统界面返回后回调。
     *
     * 运行时权限被永久拒绝时改为解释后跳设置。
     *
     * @param result 复检得到的权限结果。
     */
    fun completeRequest(result: PermissionResult) {
        val permission = requesting
        val settingsOnly = requestingSettingsOnly
        requesting = null
        if (result == PermissionResult.PermanentlyDenied && !settingsOnly && permission != null) {
            explainBeforeSettings = true
            explaining = permission
            return
        }
        finish(result)
    }

    private fun finish(result: PermissionResult) {
        val permission = activePermission
        explaining = null
        requesting = null
        requestingSettingsOnly = false
        explainBeforeSettings = false
        activePermission = null
        if (permission == AppPermission.StorageFiles) {
            onEvent(PermissionPromptEvent.PermissionResult(permission, result == PermissionResult.Granted))
        }
        val current = callback ?: return
        callback = null
        current(result)
    }

    private fun trackDialogResult(accepted: Boolean) {
        if (activePermission == AppPermission.StorageFiles) {
            onEvent(PermissionPromptEvent.DialogResult(accepted))
        }
    }
}
