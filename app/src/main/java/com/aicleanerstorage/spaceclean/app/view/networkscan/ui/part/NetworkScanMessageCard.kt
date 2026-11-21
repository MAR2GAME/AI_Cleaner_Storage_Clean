package com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part.NetworkScanNavyMuted

@Composable
internal fun NetworkScanMessageCard(
    title: String,
    message: String,
) {
    NetworkScanSectionCard(title = title) {
        Text(
            text = message,
            color = NetworkScanNavyMuted,
            fontSize = 15.sp,
            lineHeight = 20.sp,
        )
    }
}
