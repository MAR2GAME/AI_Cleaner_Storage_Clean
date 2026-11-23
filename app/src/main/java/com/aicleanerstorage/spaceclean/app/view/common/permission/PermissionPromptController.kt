package com.aicleanerstorage.spaceclean.app.view.common.permission

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionGate
import com.aicleanerstorage.spaceclean.base.permission.PermissionResult

/**
 * 权限提示状态机：解释 → 系统申请 → 结果。
 *
 * 只做状态流转，不持有弹窗，便于纯 JVM 测试。
 *
 * @param gate 权限查询与系统界面入口。
 */
@Stable
internal class PermissionPromptController(
    private val gate: PermissionGate,
) {
    /** 正在展示解释弹窗的权限；null 表示没有。 */
    var explaining by mutableStateOf<AppPermission?>(null)
        private set

    /** 已确认解释、等待系统返回的权限；null 表示没有。 */
    var requesting by mutableStateOf<AppPermission?>(null)
        private set

    private var callback: ((PermissionResult) -> Unit)? = null

    /**
     * 发起一次权限请求。
     *
     * 已授权时直接回调 [onResult]，不展示解释弹窗；同一时刻只处理一个请求。
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
        callback = onResult
        explaining = permission
    }

    /** 用户确认解释弹窗，进入系统申请。 */
    fun confirmExplanation() {
        val permission = explaining ?: return
        explaining = null
        requesting = permission
    }

    /** 用户取消解释弹窗。 */
    fun dismissExplanation() {
        finish(PermissionResult.Denied)
    }

    /**
     * 系统界面返回后回调。
     *
     * @param result 复检得到的权限结果。
     */
    fun completeRequest(result: PermissionResult) {
        requesting = null
        finish(result)
    }

    private fun finish(result: PermissionResult) {
        explaining = null
        requesting = null
        val current = callback ?: return
        callback = null
        current(result)
    }
}
