package com.aicleanerstorage.spaceclean.app.view.common.permission.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun UsageAccessPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    PermissionDialogLayout(
        title = stringResource(R.string.settings_usage_data_permission),
        description = stringResource(R.string.permission_usage_desc),
        hints = listOf(stringResource(R.string.permission_hint_usage_read)),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}
