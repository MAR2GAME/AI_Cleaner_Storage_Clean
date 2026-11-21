package com.aicleanerstorage.spaceclean.app.view.common.permission.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun OverlayPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    PermissionDialogLayout(
        title = stringResource(R.string.app_lock_overlay_permission_title),
        description = stringResource(R.string.permission_overlay_desc),
        hints =
            listOf(
                stringResource(R.string.permission_hint_overlay),
                stringResource(R.string.permission_hint_no_share_private),
            ),
        confirmText = stringResource(R.string.app_lock_overlay_permission_grant),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}
