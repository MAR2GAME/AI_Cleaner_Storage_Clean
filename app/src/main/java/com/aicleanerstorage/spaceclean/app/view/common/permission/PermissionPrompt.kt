package com.aicleanerstorage.spaceclean.app.view.common.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.aicleanerstorage.spaceclean.app.integration.Mar2Analytics
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.PermissionDialog
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.PermissionDialogCopy
import com.aicleanerstorage.spaceclean.base.permission.AppPermission
import com.aicleanerstorage.spaceclean.base.permission.PermissionRequestEffect
import com.aicleanerstorage.spaceclean.base.permission.rememberPermissionGate

/**
 * 页面级权限提示：解释弹窗 + 系统申请 + 结果回调。
 *
 * 用法：`val prompt = rememberPermissionPrompt()`，然后在需要的地方
 * `prompt.request(permission) { result -> ... }`。
 *
 * @param copy 按页面场景定制解释文案；null 时使用权限通用文案。
 * @param dialog 自定义解释弹窗；null 时使用通用 [PermissionDialog]。
 * @return 当前页面的权限提示控制器。
 */
@Composable
internal fun rememberPermissionPrompt(
    copy: (@Composable (AppPermission) -> PermissionDialogCopy)? = null,
    dialog: (@Composable (AppPermission, () -> Unit, () -> Unit) -> Unit)? = null,
): PermissionPromptController {
    val gate = rememberPermissionGate()
    val context = LocalContext.current
    val controller =
        remember(gate, context) {
            PermissionPromptController(
                gate = gate,
                requiresSettings = { permission -> permission.requiresSettings(context) },
                onEvent = { event ->
                    when (event) {
                        is PermissionPromptEvent.DialogResult -> Mar2Analytics.trackFileManagerPopup(event.accepted)
                        is PermissionPromptEvent.PermissionResult -> Mar2Analytics.trackFilePermissionResult(event.accepted)
                    }
                },
            )
        }

    PermissionRequestEffect(
        request = controller.requesting,
        settingsOnly = controller.requestingSettingsOnly,
    ) { result ->
        controller.completeRequest(result)
    }
    controller.explaining?.let { permission ->
        if (dialog == null) {
            PermissionDialog(
                copy = copy?.invoke(permission) ?: permission.dialogCopy(),
                onConfirm = controller::confirmExplanation,
                onDismiss = controller::dismissExplanation,
            )
        } else {
            dialog(permission, controller::confirmExplanation, controller::dismissExplanation)
        }
    }
    return controller
}
