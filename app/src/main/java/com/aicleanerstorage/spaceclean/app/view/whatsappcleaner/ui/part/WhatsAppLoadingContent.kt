package com.aicleanerstorage.spaceclean.app.view.whatsappcleaner.ui.part

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.designsystem.component.ToolboxScanningContent

@Composable
internal fun WhatsAppLoadingContent() {
    ToolboxScanningContent(
        centerIconRes = R.drawable.ic_scan_whatsapp_cleanner,
        captionText = stringResource(R.string.scanning_whatsapp_files),
        modifier = Modifier.fillMaxSize(),
        centerIconSize = WhatsAppScanCenterIconSize,
    )
}

private val WhatsAppScanCenterIconSize = 74.dp
