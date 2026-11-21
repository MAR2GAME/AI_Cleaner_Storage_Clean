package com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.part

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.devicekit.model.DeviceHardwareInfo
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.orPlaceholder

@Composable
internal fun ScreenCard(hardware: DeviceHardwareInfo) {
    InfoSection(
        title = stringResource(R.string.device_screen),
        rows =
            listOf(
                stringResource(R.string.device_screen_size) to hardware.screenSize.orPlaceholder(),
                stringResource(R.string.device_screen_density) to hardware.screenDensity.orPlaceholder(),
                stringResource(R.string.device_screen_multi_touch) to supportText(hardware.multiTouchSupported),
            ),
    )
}

@Composable
internal fun InfoSection(
    title: String,
    rows: List<Pair<String, String>>,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
        ) {
            SectionHeader(title)
            Spacer(modifier = Modifier.height(20.dp))
            rows.forEachIndexed { index, (label, value) ->
                InfoRow(label, value)
                if (index != rows.lastIndex) {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            color = DeviceTextColor,
            fontSize = 18.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = DeviceTextColor,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

@Composable
internal fun supportText(supported: Boolean): String =
    if (supported) {
        stringResource(R.string.device_supported)
    } else {
        stringResource(R.string.device_not_supported)
    }
