package com.aicleanerstorage.spaceclean.app.view.common.permission.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R

/**
 * 所有权限解释弹窗共用的骨架。
 * 每个权限传入自己的标题、描述、提示列表和按钮文案。
 */
@Composable
internal fun PermissionDialogLayout(
    title: String,
    description: String,
    hints: List<String> = emptyList(),
    confirmText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column {
                Text(text = description)
                hints.forEach { hint ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• $hint",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText ?: stringResource(R.string.grant))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        },
    )
}
