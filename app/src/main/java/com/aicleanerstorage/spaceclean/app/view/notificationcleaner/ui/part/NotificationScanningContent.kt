package com.aicleanerstorage.spaceclean.app.view.notificationcleaner.ui.part

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.ToolboxScanningContent

@Composable
internal fun NotificationScanningContent() {
    ToolboxScanningContent(
        centerIconRes = R.drawable.ic_scan_notification_clean,
        captionText = stringResource(R.string.scan_loading_fallback),
        modifier = Modifier.fillMaxSize(),
    )
}
