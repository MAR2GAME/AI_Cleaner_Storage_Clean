package com.aicleanerstorage.spaceclean.feature.notificationcleaner.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.popups.CleanXDecisionDialog

@Composable
internal fun NotificationBlockingTurnedOffDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    CleanXDecisionDialog(
        title = stringResource(R.string.notification_blocking_off_message),
        message = stringResource(R.string.notification_blocking_off_detail),
        onDismissRequest = onCancel,
        dismissText = stringResource(R.string.cancel),
        onDismissAction = onCancel,
        confirmText = stringResource(R.string.ok),
        onConfirmAction = onConfirm,
    )
}
