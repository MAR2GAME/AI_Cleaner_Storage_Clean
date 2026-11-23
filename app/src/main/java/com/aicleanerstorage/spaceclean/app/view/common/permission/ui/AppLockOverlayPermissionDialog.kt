package com.aicleanerstorage.spaceclean.app.view.common.permission.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.PermissionDialog
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.PermissionDialogCopy

/** 应用锁悬浮窗权限解释；带插画，与旧版视觉一致。 */
@Composable
internal fun AppLockOverlayPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    PermissionDialog(
        copy =
            PermissionDialogCopy(
                title = stringResource(R.string.app_lock_overlay_permission_message),
                description = null,
                confirmText = stringResource(R.string.manage_permission),
                cancelText = stringResource(R.string.cancel),
                heroImageRes = R.drawable.app_lock_permission_figma,
            ),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}
