package com.aicleanerstorage.spaceclean.app.view.deviceinfo.ui.part

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.DeviceInfoUiState
import kotlin.math.roundToInt

@Composable
internal fun StateInfoCard(uiState: DeviceInfoUiState) {
    val animatedCpuProgress by animateFloatAsState(
        targetValue = uiState.cpuUsagePercent?.let { it / 100f } ?: 0f,
        animationSpec = tween(500),
        label = "cpuProgress",
    )
    val animatedRamProgress by animateFloatAsState(
        targetValue = uiState.memory.usagePercent / 100f,
        animationSpec = tween(500),
        label = "ramProgress",
    )
    val animatedStorageProgress by animateFloatAsState(
        targetValue = uiState.storage.usagePercent / 100f,
        animationSpec = tween(500),
        label = "storageProgress",
    )
    val cpuUsageText = uiState.cpuUsagePercent?.let { "$it%" } ?: "--"
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
        ) {
            SectionHeader(stringResource(R.string.device_state_info))
            Spacer(modifier = Modifier.height(20.dp))
            ProgressInfoRow(
                label = "${stringResource(R.string.device_progress_cpu_label)}$cpuUsageText/1`C",
                progress = animatedCpuProgress,
                color = DeviceCpuProgressColor,
                trackColor = DeviceCpuProgressTrackColor,
            )
            Spacer(modifier = Modifier.height(20.dp))
            ProgressInfoRow(
                label = "${stringResource(R.string.device_progress_ram_label)}${formatCompactBytes(uiState.memory.usedBytes)}/${formatCompactBytes(uiState.memory.totalBytes)}",
                progress = animatedRamProgress,
                color = DeviceRamProgressColor,
                trackColor = DeviceRamProgressTrackColor,
            )
            Spacer(modifier = Modifier.height(20.dp))
            ProgressInfoRow(
                label = "${stringResource(R.string.device_progress_storage_label)}${formatCompactBytes(uiState.storage.usedBytes)}/${formatCompactBytes(uiState.storage.totalBytes)}",
                progress = animatedStorageProgress,
                color = DeviceStorageProgressColor,
                trackColor = DeviceStorageProgressTrackColor,
            )
        }
    }
}

@Composable
private fun ProgressInfoRow(
    label: String,
    progress: Float?,
    color: Color,
    trackColor: Color,
) {
    Column {
        Text(
            text = label,
            color = DeviceTextColor,
            fontSize = 18.sp,
            lineHeight = 24.sp,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(trackColor, RoundedCornerShape(50)),
        ) {
            progress?.let { value ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(value.coerceIn(0f, 1f))
                            .height(12.dp)
                            .background(color, RoundedCornerShape(50)),
                )
            }
        }
    }
}

private val DeviceCpuProgressColor = Color(0xFF5DC9D5)

private val DeviceCpuProgressTrackColor = Color(0xFFEEF4F9)

private val DeviceRamProgressColor = Color(0xFFD5C75D)

private val DeviceRamProgressTrackColor = Color(0xFFF9F6EE)

private val DeviceStorageProgressColor = Color(0xFF5D5DD5)

private val DeviceStorageProgressTrackColor = Color(0xFFEEF9F0)

private val DeviceDividerColor = Color(0x33282927)

@Composable
internal fun SectionHeader(text: String) {
    Column {
        Text(
            text = text,
            color = DeviceTextColor,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(end = 12.dp)
                    .height(0.5.dp)
                    .background(DeviceDividerColor),
        )
    }
}

private fun formatCompactBytes(bytes: Long): String {
    if (bytes <= 0L) return "0B"
    val gb = bytes / (1024f * 1024f * 1024f)
    val mb = bytes / (1024f * 1024f)
    return if (gb >= 1f) {
        "${(gb * 10f).roundToInt() / 10f}GB"
    } else {
        "${mb.roundToInt()}MB"
    }
}
