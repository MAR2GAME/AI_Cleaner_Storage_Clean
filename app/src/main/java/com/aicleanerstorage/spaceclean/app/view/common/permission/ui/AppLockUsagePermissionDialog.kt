package com.aicleanerstorage.spaceclean.app.view.common.permission.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.designsystem.component.CircularAppLogo
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.PermissionDialog
import com.aicleanerstorage.spaceclean.base.designsystem.component.popups.PermissionDialogCopy

private val UsagePermissionText = Color(0xFF282927)
private val UsagePermissionGreen = Color(0xFF5DD595)
private val UsagePermissionCard = Color(0xFFF8FFF8)

/** 应用锁使用情况权限解释；带应用权限卡，与旧版视觉一致。 */
@Composable
internal fun AppLockUsagePermissionDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    PermissionDialog(
        copy =
            PermissionDialogCopy(
                title = stringResource(R.string.app_lock_usage_permission_title),
                description = stringResource(R.string.app_lock_usage_permission_desc),
                confirmText = stringResource(R.string.submit),
                cancelText = null,
            ),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        footer = { AppPermissionCard() },
    )
}

@Composable
private fun AppPermissionCard() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(UsagePermissionCard)
                .padding(horizontal = 16.dp, vertical = 18.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularAppLogo(modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.app_name),
                color = UsagePermissionGreen,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.app_lock_usage_permission_grant),
            color = UsagePermissionText,
            fontSize = 16.sp,
        )
    }
}
