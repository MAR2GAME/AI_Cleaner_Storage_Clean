package com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.*

import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.ui.*

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.device.model.DeviceHardwareInfo
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.common.ui.components.stableNavigationBarsPadding
import com.aicleanerstorage.spaceclean.common.ui.components.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXMutedText
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.ui.ToolboxScanningContent
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.DeviceInfoUiState
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.currentBatteryCapacityMah
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.formatBatteryCapacityMah
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.formatBatteryTemperature
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.formatBatteryVoltage
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.orPlaceholder
import kotlin.math.roundToInt

@Composable
fun DeviceInfoScreen(
    state: DeviceInfoUiState,
    onAction: (DeviceInfoAction) -> Unit,
) {
    CleanXScaffoldPage(
        title = stringResource(R.string.device_model),
        backgroundBrush = Brush.linearGradient(
            colors = listOf(DevicePageBackground, DevicePageBackground),
        ),
        onBack = { onAction(DeviceInfoAction.Back) },
        contentPadding = if (state.isScanning) PaddingValues(0.dp) else PaddingValues(horizontal = 16.dp),
        scrollEnabled = !state.isScanning,
        verticalArrangement = Arrangement.Top,
    ) {
        if (state.isScanning) {
            ToolboxScanningContent(
                centerIconRes = R.drawable.ic_scan_device_info,
                captionText = stringResource(R.string.onboarding_checking_device_info),
            )
        } else {
            DeviceModelCard(state.hardware)
            Spacer(modifier = Modifier.height(16.dp))
            StateInfoCard(state)
            Spacer(modifier = Modifier.height(20.dp))
            ScreenCard(state.hardware)
            Spacer(modifier = Modifier.height(20.dp))
            BatteryCard(state)
            Spacer(modifier = Modifier.height(20.dp))
            SensorsCard(state.hardware)
            Spacer(modifier = Modifier.height(20.dp))
            CpuCard(state.hardware)
            Spacer(modifier = Modifier.stableNavigationBarsPadding().height(24.dp))
        }
    }

    if (state.showStopDialog) {
        StopScanDialog(
            onQuit = { onAction(DeviceInfoAction.QuitScan) },
            onResume = { onAction(DeviceInfoAction.ResumeScan) },
        )
    }
}

@Composable
private fun DeviceModelCard(hardware: DeviceHardwareInfo) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(132.dp),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                SummaryTextLine(
                    label = stringResource(R.string.home_device_model),
                    value = deviceModelName(hardware.model),
                )
                Spacer(modifier = Modifier.height(8.dp))
                SummaryTextLine(
                    label = stringResource(R.string.home_system_version),
                    value = hardware.androidVersion.orPlaceholder(),
                )
            }

            Spacer(Modifier.weight(1.0f))

            Box(
                Modifier.padding(2.72727.dp)
                    .width(120.dp)
                    .height(96.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.device_model),
                    contentDescription = null,
                    contentScale = ContentScale.None
                )
            }

        }
    }
}

@Composable
private fun StateInfoCard(uiState: DeviceInfoUiState) {
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
    val cpuTemperatureText = formatBatteryTemperature(uiState.cpuTemperatureC, uiState.temperatureUnit, includeSpace = false)
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
private val DevicePageBackground = Color(0xFFF4F4F4)
private val DeviceTextColor = Color(0xFF282927)
private val DeviceDividerColor = Color(0x33282927)

@Composable
private fun InfoSection(
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
private fun SummaryTextLine(
    label: String,
    value: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = CleanXMutedText,
            fontSize = 14.sp,
            lineHeight = 17.sp,
        )
        Text(
            text = value,
            color = DeviceTextColor,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ScreenCard(hardware: DeviceHardwareInfo) {
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
private fun BatteryCard(uiState: DeviceInfoUiState) {
    InfoSection(
        title = stringResource(R.string.device_battery),
        rows =
            listOf(
                stringResource(R.string.battery_health_status) to localizedBatteryHealth(uiState.battery.health),
                stringResource(R.string.battery_current_capacity) to
                    formatBatteryCapacityMah(currentBatteryCapacityMah(uiState.battery)),
                stringResource(R.string.battery_total_capacity) to formatBatteryCapacityMah(uiState.battery.capacity),
                stringResource(R.string.battery_voltage) to formatBatteryVoltage(uiState.battery.voltage),
                stringResource(R.string.battery_temperature) to
                    formatBatteryTemperature(uiState.battery.temperature, uiState.temperatureUnit),
                stringResource(R.string.battery_status) to localizedBatteryStatus(uiState.batteryStatus.statusText),
                stringResource(R.string.battery_charging_status) to
                    if (uiState.batteryStatus.isCharging) {
                        stringResource(R.string.battery_charging)
                    } else {
                        stringResource(R.string.battery_not_charging)
                    },
                stringResource(R.string.battery_technology) to uiState.battery.technology.orPlaceholder(),
            ),
    )
}

@Composable
private fun SensorsCard(hardware: DeviceHardwareInfo) {
    InfoSection(
        title = stringResource(R.string.device_sensors),
        rows =
            listOf(
                stringResource(R.string.device_accelerometer_sensor) to supportText(hardware.sensors.accelerometer),
                stringResource(R.string.device_magnetic_field_sensor) to supportText(hardware.sensors.magneticField),
                stringResource(R.string.device_orientation_sensor) to supportText(hardware.sensors.orientation),
                stringResource(R.string.device_gyroscope_sensor) to supportText(hardware.sensors.gyroscope),
                stringResource(R.string.device_light_sensor) to supportText(hardware.sensors.light),
                stringResource(R.string.device_distance_sensor) to supportText(hardware.sensors.proximity),
                stringResource(R.string.device_temperature_sensor) to supportText(hardware.sensors.ambientTemperature),
            ),
    )
}

@Composable
private fun CpuCard(hardware: DeviceHardwareInfo) {
    val cpuCoreCount =
        hardware.cpu.cores
            .takeIf { it > 0 }
            ?.toString()
            ?: "--"
    InfoSection(
        title = stringResource(R.string.device_cpu),
        rows =
            listOf(
                stringResource(R.string.device_cpu_hardware) to hardware.cpu.hardware.orPlaceholder(),
                stringResource(R.string.device_cpu_model) to hardware.cpu.model.orPlaceholder(),
                stringResource(R.string.device_cpu_cores) to cpuCoreCount,
                stringResource(R.string.device_cpu_frequency) to hardware.cpu.maxFrequency.orPlaceholder(),
            ),
    )
}

@Composable
private fun SectionHeader(text: String) {
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
private fun supportText(supported: Boolean): String =
    if (supported) {
        stringResource(R.string.device_supported)
    } else {
        stringResource(R.string.device_not_supported)
    }

@Composable
private fun localizedBatteryHealth(health: String): String =
    when (health) {
        "Good" -> stringResource(R.string.battery_health_good)
        "Cold" -> stringResource(R.string.battery_health_cold)
        "Dead" -> stringResource(R.string.battery_health_dead)
        "Overheat" -> stringResource(R.string.battery_health_overheat)
        "Overvoltage" -> stringResource(R.string.battery_health_overvoltage)
        "Failure" -> stringResource(R.string.battery_health_failure)
        else -> stringResource(R.string.device_unknown)
    }

@Composable
private fun localizedBatteryStatus(status: String): String =
    when (status) {
        "Charging" -> stringResource(R.string.battery_charging)
        "Discharging" -> stringResource(R.string.battery_discharging)
        "Full" -> stringResource(R.string.battery_full)
        "Not Charging" -> stringResource(R.string.battery_not_charging)
        else -> stringResource(R.string.device_unknown)
    }

private fun deviceModelName(model: String): String {
    val resolvedModel = model.orPlaceholder()
    val manufacturer = Build.MANUFACTURER.orEmpty().trim()
    if (resolvedModel == "--" || manufacturer.isBlank()) return resolvedModel
    return if (resolvedModel.contains(manufacturer, ignoreCase = true)) {
        resolvedModel
    } else {
        "$manufacturer-$resolvedModel"
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
