package com.aicleanerstorage.spaceclean.app.view.common.permission.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun LocationPermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    PermissionDialogLayout(
        title = stringResource(R.string.settings_location_permission),
        description = stringResource(R.string.permission_location_desc),
        hints = listOf(stringResource(R.string.permission_hint_network_scan)),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}
