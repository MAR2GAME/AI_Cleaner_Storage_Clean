package com.aicleanerstorage.spaceclean.app.view.common.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.PermissionDialog
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionRequestEffect
import com.aicleanerstorage.spaceclean.base.permission.rememberPermissionGate

/**
 * 页面级权限提示：解释弹窗 + 系统申请 + 结果回调。
 *
 * 用法：`val prompt = rememberPermissionPrompt()`，然后在需要的地方
 * `prompt.request(permission) { result -> ... }`。
 *
 * @param dialog 自定义解释弹窗；null 时使用通用 [PermissionDialog]。
 * @return 当前页面的权限提示控制器。
 */
@Composable
internal fun rememberPermissionPrompt(
    dialog: (@Composable (AppPermission, () -> Unit, () -> Unit) -> Unit)? = null,
): PermissionPromptController {
    val gate = rememberPermissionGate()
    val controller = remember(gate) { PermissionPromptController(gate) }

    PermissionRequestEffect(controller.requesting) { result ->
        controller.completeRequest(result)
    }
    controller.explaining?.let { permission ->
        if (dialog == null) {
            PermissionDialog(
                copy = permission.dialogCopy(),
                onConfirm = controller::confirmExplanation,
                onDismiss = controller::dismissExplanation,
            )
        } else {
            dialog(permission, controller::confirmExplanation, controller::dismissExplanation)
        }
    }
    return controller
}
