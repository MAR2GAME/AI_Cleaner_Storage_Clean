package com.aicleanerstorage.spaceclean.app.view.common.permission.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun NotificationListenerPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    PermissionDialogLayout(
        title = stringResource(R.string.settings_notification_permission),
        description = stringResource(R.string.permission_notification_desc),
        hints = listOf(stringResource(R.string.permission_hint_notifications)),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}
