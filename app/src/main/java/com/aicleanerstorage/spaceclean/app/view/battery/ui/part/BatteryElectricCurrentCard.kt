package com.aicleanerstorage.spaceclean.app.view.battery.ui.part

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.aicleanerstorage.spaceclean.app.view.battery.ui.BatteryChartAxisBounds
import com.aicleanerstorage.spaceclean.app.view.battery.ui.BatteryChartPoint
import com.aicleanerstorage.spaceclean.app.view.battery.ui.batteryCurrentAxisBounds
import com.aicleanerstorage.spaceclean.app.view.battery.ui.batteryCurrentAxisLabels
import com.aicleanerstorage.spaceclean.app.view.battery.ui.batteryCurrentChartMaxPoints
import com.aicleanerstorage.spaceclean.app.view.battery.ui.batteryCurrentChartPoints
import com.aicleanerstorage.spaceclean.app.view.battery.ui.batteryCurrentChartWindowEndMillis
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.BatteryCurrentRange
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.BatteryCurrentSample
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.DeviceInfoUiState
import com.aicleanerstorage.spaceclean.app.view.common.toolbox.interaction.formatBatteryCurrent
import java.util.Locale

@Composable
internal fun BatteryElectricCurrentCard(
    uiState: DeviceInfoUiState,
    onRangeSelected: (BatteryCurrentRange) -> Unit,
) {
    BatteryChartCard(
        title = stringResource(R.string.battery_electric_current),
        rows =
            listOf(
                stringResource(R.string.battery_realtime_current) to formatBatteryCurrent(uiState.displayCurrentNow),
                stringResource(R.string.battery_average_current) to formatBatteryCurrent(uiState.selectedCurrentAverage),
            ),
    ) {
        BatteryCurrentChart(
            samples = uiState.selectedCurrentSamples,
            selectedRange = uiState.selectedCurrentRange,
            latestSampleTimestampMillis = uiState.latestSampleTimestampMillis,
        )
        Spacer(modifier = Modifier.height(20.dp))
        BatteryTimeTabs(
            selectedRange = uiState.selectedCurrentRange,
            onRangeSelected = onRangeSelected,
        )
    }
}

private val TabBackground = BatteryText.copy(alpha = 0.05f)

@Composable
private fun BatteryCurrentChart(
    samples: List<BatteryCurrentSample>,
    selectedRange: BatteryCurrentRange,
    latestSampleTimestampMillis: Long,
) {
    val windowEndMillis =
        currentChartWindowEndMillis(
            samples = samples,
            selectedRange = selectedRange,
            latestSampleTimestampMillis = latestSampleTimestampMillis,
        )
    val chartPoints =
        currentChartPoints(
            samples = samples,
            selectedRange = selectedRange,
            windowEndMillis = windowEndMillis,
        )
    BatteryLineChart(
        points = chartPoints,
        windowEndMillis = windowEndMillis,
        windowMillis = selectedRange.durationMillis,
        xLabels = currentAxisLabels(selectedRange),
        axisBounds = currentAxisBounds(chartPoints.map { it.value }),
        valueLabelFormatter = { String.format(Locale.US, "%.2f mA", it) },
        maxPoints = currentChartMaxPoints(selectedRange),
    )
}

@Composable
private fun BatteryTimeTabs(
    selectedRange: BatteryCurrentRange,
    onRangeSelected: (BatteryCurrentRange) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(30.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(TabBackground),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BatteryCurrentRange.entries.forEachIndexed { index, range ->
            val selected = range == selectedRange
            val segmentShape =
                when (index) {
                    0 -> RoundedCornerShape(topStart = 15.dp, bottomStart = 15.dp)
                    BatteryCurrentRange.entries.lastIndex ->
                        RoundedCornerShape(topEnd = 15.dp, bottomEnd = 15.dp)

                    else -> RoundedCornerShape(0.dp)
                }
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(segmentShape)
                        .background(if (selected) BatteryGreen else Color.Transparent)
                        .clickable { onRangeSelected(range) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(range.labelRes),
                    color = if (selected) Color.White else BatteryText.copy(alpha = 0.35f),
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                )
            }
        }
    }
}

private fun currentAxisLabels(range: BatteryCurrentRange): List<String> =
    batteryCurrentAxisLabels(range)

private fun currentChartMaxPoints(range: BatteryCurrentRange): Int? =
    batteryCurrentChartMaxPoints(range)

private fun currentChartPoints(
    samples: List<BatteryCurrentSample>,
    selectedRange: BatteryCurrentRange,
    windowEndMillis: Long,
): List<BatteryChartPoint> = batteryCurrentChartPoints(samples, selectedRange, windowEndMillis)

private fun currentChartWindowEndMillis(
    samples: List<BatteryCurrentSample>,
    selectedRange: BatteryCurrentRange,
    latestSampleTimestampMillis: Long,
): Long = batteryCurrentChartWindowEndMillis(samples, selectedRange, latestSampleTimestampMillis)

private fun currentAxisBounds(values: List<Float>): BatteryChartAxisBounds =
    batteryCurrentAxisBounds(values)
