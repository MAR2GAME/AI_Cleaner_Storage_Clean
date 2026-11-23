package com.aicleanerstorage.spaceclean.app.view.common.permission

import androidx.annotation.StringRes
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

/** 垃圾清理的权限说明，沿用 v1.0.2 的“扫描和清理垃圾文件”文案。 */
@Composable
internal fun AppPermission.junkCleanCopy(): PermissionDialogCopy =
    actionStorageCopy(
        descriptionRes = R.string.permission_storage_desc,
        hintRes = R.string.permission_hint_junk_deleted,
    )

/** 病毒深扫的权限说明，沿用 v1.0.2 的“扫描文件风险”文案。 */
@Composable
internal fun AppPermission.virusDeepScanCopy(): PermissionDialogCopy =
    actionStorageCopy(
        descriptionRes = R.string.permission_virus_storage_desc,
        hintRes = R.string.permission_hint_threat_files,
    )

/** WhatsApp 清理的权限说明，沿用 v1.0.2 的 WhatsApp 文案。 */
@Composable
internal fun AppPermission.whatsAppCopy(): PermissionDialogCopy =
    actionStorageCopy(
        descriptionRes = R.string.permission_whatsapp_storage_desc,
        hintRes = R.string.permission_hint_whatsapp_files,
    )

/** 网络用量的权限说明，沿用 v1.0.2 的“网络用量详情”文案。 */
@Composable
internal fun AppPermission.networkUsageCopy(): PermissionDialogCopy = actionUsageCopy(R.string.permission_network_usage_desc)

@Composable
private fun AppPermission.actionStorageCopy(
    @StringRes descriptionRes: Int,
    @StringRes hintRes: Int,
): PermissionDialogCopy =
    PermissionDialogCopy(
        title = stringResource(R.string.permission_title_required),
        description = stringResource(descriptionRes),
        confirmText = stringResource(R.string.allow),
        cancelText = stringResource(R.string.cancel),
        hints =
            listOf(
                PermissionHint(R.drawable.ic_permission_junk, stringResource(hintRes)),
                PermissionHint(R.drawable.ic_permission_privacy, stringResource(R.string.permission_hint_no_personal)),
            ),
    )

@Composable
private fun AppPermission.actionUsageCopy(
    @StringRes descriptionRes: Int,
): PermissionDialogCopy =
    PermissionDialogCopy(
        title = stringResource(R.string.permission_title_required),
        description = stringResource(descriptionRes),
        confirmText = stringResource(R.string.allow),
        cancelText = stringResource(R.string.cancel),
        hints =
            listOf(
                PermissionHint(R.drawable.ic_permission_junk, stringResource(R.string.permission_hint_usage_read)),
                PermissionHint(R.drawable.ic_permission_privacy, stringResource(R.string.permission_hint_no_personal)),
            ),
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
