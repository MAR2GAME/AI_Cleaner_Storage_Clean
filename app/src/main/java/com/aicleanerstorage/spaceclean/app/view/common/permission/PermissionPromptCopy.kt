package com.aicleanerstorage.spaceclean.app.view.common.permission

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.PermissionDialogCopy
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.PermissionHint
import com.aicleanerstorage.spaceclean.base.permission.AppPermission

/**
 * 通用权限解释弹窗的文案。
 *
 * 与旧版保持一致：统一“需要权限”标题、两句图标要点、允许/取消。
 */
@Composable
internal fun AppPermission.dialogCopy(): PermissionDialogCopy =
    PermissionDialogCopy(
        title = stringResource(R.string.permission_title_required),
        description = stringResource(descriptionRes()),
        confirmText = stringResource(R.string.allow),
        cancelText = stringResource(R.string.cancel),
        hints = hintList(),
    )

@Composable
private fun AppPermission.hintList(): List<PermissionHint> =
    when (this) {
        AppPermission.StorageFiles ->
            listOf(
                PermissionHint(R.drawable.ic_permission_junk, stringResource(R.string.permission_hint_files_safe)),
                PermissionHint(R.drawable.ic_permission_privacy, stringResource(R.string.permission_hint_no_personal)),
            )
        AppPermission.Location ->
            listOf(
                PermissionHint(R.drawable.ic_permission_privacy, stringResource(R.string.permission_hint_network_scan)),
                PermissionHint(R.drawable.ic_permission_privacy, stringResource(R.string.permission_hint_no_personal)),
            )
        AppPermission.UsageAccess ->
            listOf(
                PermissionHint(R.drawable.ic_permission_junk, stringResource(R.string.permission_hint_usage_read)),
                PermissionHint(R.drawable.ic_permission_privacy, stringResource(R.string.permission_hint_no_personal)),
            )
        AppPermission.NotificationListener ->
            listOf(
                PermissionHint(R.drawable.ic_permission_privacy, stringResource(R.string.permission_hint_notifications)),
                PermissionHint(R.drawable.ic_permission_privacy, stringResource(R.string.permission_hint_no_personal)),
            )
        AppPermission.Overlay ->
            listOf(
                PermissionHint(R.drawable.ic_permission_privacy, stringResource(R.string.permission_hint_overlay)),
                PermissionHint(R.drawable.ic_permission_privacy, stringResource(R.string.permission_hint_no_personal)),
            )
        AppPermission.MediaLocation,
        AppPermission.PostNotifications,
        ->
            listOf(
                PermissionHint(R.drawable.ic_permission_privacy, stringResource(R.string.permission_hint_no_personal)),
            )
    }
