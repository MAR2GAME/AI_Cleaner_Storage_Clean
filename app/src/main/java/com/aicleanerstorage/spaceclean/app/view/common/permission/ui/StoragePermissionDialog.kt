package com.aicleanerstorage.spaceclean.app.view.common.permission.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun StoragePermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    PermissionDialogLayout(
        title = stringResource(R.string.settings_storage_permission),
        description = stringResource(R.string.permission_storage_files_desc),
        hints =
            listOf(
                stringResource(R.string.permission_hint_junk_deleted),
                stringResource(R.string.permission_hint_files_safe),
            ),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}
