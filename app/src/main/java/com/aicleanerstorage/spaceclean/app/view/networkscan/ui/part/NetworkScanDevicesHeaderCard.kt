package com.aicleanerstorage.spaceclean.app.view.networkscan.ui.part

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.NetworkScanCardBg
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.NetworkScanCardRadius
import com.aicleanerstorage.spaceclean.app.view.networkscan.ui.NetworkScanNavy

@Composable
internal fun NetworkScanDevicesHeaderCard(count: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = NetworkScanCardBg,
        shape = RoundedCornerShape(NetworkScanCardRadius),
    ) {
        Text(
            text = stringResource(R.string.devices_count, count),
            color = NetworkScanNavy,
            fontSize = 18.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        )
    }
}
