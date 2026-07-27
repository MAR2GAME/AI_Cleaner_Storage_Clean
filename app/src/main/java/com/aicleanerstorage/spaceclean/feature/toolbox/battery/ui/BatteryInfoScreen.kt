package com.aicleanerstorage.spaceclean.feature.toolbox.battery.ui

import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.*
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.BatteryInfoAction

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.common.ui.components.stableNavigationBarsPadding
import com.aicleanerstorage.spaceclean.common.ui.components.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.ui.ToolboxScanningContent
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.BatteryCurrentRange
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.BatteryCurrentSample
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.DeviceInfoUiState
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.displayBatteryTemperatureValue
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.formatBatteryCurrent
import com.aicleanerstorage.spaceclean.feature.toolbox.deviceinfo.formatBatteryTemperature
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.ui.BatteryChartAxisBounds
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.ui.BatteryChartPoint
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.ui.batteryCurrentAxisBounds
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.ui.batteryCurrentChartMaxPoints
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.ui.batteryCurrentChartPoints
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.ui.batteryCurrentChartWindowEndMillis
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.ui.batteryCurrentAxisLabels
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.ui.batteryOneMinuteWindowEnd
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.ui.batteryTemperatureAxisBounds
import com.aicleanerstorage.spaceclean.feature.toolbox.battery.ui.downsampleBatteryChartPoints
import java.util.Locale
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

private val BatteryPageBackground = Color(0xFFF4F4F4)
private val BatteryText = Color(0xFF282927)
private val BatteryMutedText = BatteryText.copy(alpha = 0.65f)
private val BatteryMetricLabel = Color(0xFF999999)
private val BatteryDivider = BatteryText.copy(alpha = 0.20f)
private val BatteryGreen = Color(0xFF5DD595)
private val ChartGrid = Color(0xFF9AA6B8).copy(alpha = 0.72f)
private val ChartFill = BatteryGreen.copy(alpha = 0.14f)
private val TabBackground = BatteryText.copy(alpha = 0.05f)

@Composable
fun BatteryInfoScreen(
    state: DeviceInfoUiState,
    onAction: (BatteryInfoAction) -> Unit,
) {
    CleanXScaffoldPage(
        title = stringResource(R.string.battery_info),
        backgroundBrush =
            Brush.linearGradient(
                colors = listOf(BatteryPageBackground, BatteryPageBackground),
            ),
        onBack = { onAction(BatteryInfoAction.Back) },
        contentPadding = if (state.isScanning) PaddingValues(0.dp) else PaddingValues(horizontal = 16.dp),
        scrollEnabled = !state.isScanning,
        verticalArrangement = Arrangement.Top,
    ) {
        if (state.isScanning) {
            ToolboxScanningContent(
                centerIconRes = R.drawable.ic_scan_battery_info,
                captionText = stringResource(R.string.battery_info),
            )
        } else {
            BatteryTopCard(
                statusText = localizedBatteryStatus(state.batteryStatus.statusText),
                capacityText = state.batteryCapacityText,
            )
            Spacer(modifier = Modifier.height(16.dp))
            BatteryInformationCard(state)
            Spacer(modifier = Modifier.height(20.dp))
            BatteryTemperatureCard(state)
            Spacer(modifier = Modifier.height(20.dp))
            BatteryElectricCurrentCard(
                uiState = state,
                onRangeSelected = { onAction(BatteryInfoAction.RangeSelected(it)) },
            )
            Spacer(modifier = Modifier
                .stableNavigationBarsPadding()
                .height(24.dp))
        }
    }

    if (state.showStopDialog) {
        StopScanDialog(
            onQuit = { onAction(BatteryInfoAction.QuitScan) },
            onResume = { onAction(BatteryInfoAction.ResumeScan) },
        )
    }
}

@Composable
private fun BatteryTopCard(
    statusText: String,
    capacityText: String,
) {
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
                modifier = Modifier
                    .padding(10.dp, 0.dp, 0.dp, 0.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                BatterySummaryLine(
                    label = stringResource(R.string.battery_status),
                    value = statusText,
                )
                BatterySummaryLine(
                    label = stringResource(R.string.battery_capacity),
                    value = capacityText,
                )
            }
            Spacer(Modifier.weight(1.0f))
            Box(
                Modifier.padding(0.dp)
                .width(103.63672.dp)
                .height(60.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.battery_info),
                    contentDescription = null,
                    contentScale = ContentScale.None
                )
            }
        }
    }
}

@Composable
private fun BatterySummaryLine(
    label: String,
    value: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = BatteryMutedText,
            fontSize = 14.sp,
            lineHeight = 17.sp,
        )
        Text(
            text = value,
            color = BatteryText,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun BatteryInformationCard(uiState: DeviceInfoUiState) {
    val voltageValue =
        uiState.battery.voltage.takeIf { it > 0 }?.let {
            String.format(Locale.US, "%.1f", it / 1000f)
        } ?: "--"
    val metrics =
        listOf(
            BatteryMetricUi(
                iconRes = R.drawable.temperature,
                iconSizeDp = 32,
                label = stringResource(R.string.battery_temperature),
                value = String.format(Locale.US, "%.1f", uiState.displayTemperatureC),
                unit = "\u00B0${uiState.temperatureUnit}",
            ),
            BatteryMetricUi(
                iconRes = R.drawable.voltage,
                label = stringResource(R.string.battery_voltage),
                value = voltageValue,
                unit = if (voltageValue == "--") null else "V",
            ),
            BatteryMetricUi(
                iconRes = R.drawable.battery_health,
                label = stringResource(R.string.battery_health),
                value = localizedBatteryHealth(uiState.battery.health),
            ),
            BatteryMetricUi(
                iconRes = R.drawable.life,
                label = stringResource(R.string.battery_life),
                value = uiState.batteryLifeText,
            ),
        )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
        ) {
            BatterySectionHeader(stringResource(R.string.battery_information))
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                metrics.forEach { metric ->
                    BatteryMetricItem(
                        metric = metric,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun BatteryMetricItem(
    metric: BatteryMetricUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(metric.iconRes),
                contentDescription = null,
                modifier = Modifier.size(metric.iconSizeDp.dp),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = metric.label.uppercase(),
            color = BatteryMetricLabel,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = metric.value,
                color = BatteryText,
                fontSize = if (metric.value.length > 8) 16.sp else 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
            metric.unit?.let { unit ->
                Text(
                    text = unit,
                    color = BatteryText,
                    fontSize = 10.sp,
                    lineHeight = 18.sp,
                    maxLines = 1,
                )
            }
        }
    }
}

private data class BatteryMetricUi(
    val iconRes: Int,
    val label: String,
    val value: String,
    val unit: String? = null,
    val iconSizeDp: Int = 40,
)

@Composable
private fun BatteryTemperatureCard(uiState: DeviceInfoUiState) {
    BatteryChartCard(
        title = stringResource(R.string.battery_temperature_title),
        rows =
            listOf(
                stringResource(R.string.battery_realtime_temperature) to
                        formatBatteryTemperature(
                            uiState.displayTemperatureC,
                            uiState.temperatureUnit,
                            includeSpace = false
                        ),
                stringResource(R.string.battery_average_temperature) to
                        formatBatteryTemperature(
                            uiState.selectedTemperatureAverageC,
                            uiState.temperatureUnit,
                            includeSpace = false
                        ),
            ),
    ) {
        val chartPoints =
            uiState.selectedTemperatureSamples.map {
                ChartPoint(
                    timestampMillis = it.timestampMillis,
                    value = displayBatteryTemperatureValue(
                        it.temperatureC,
                        uiState.temperatureUnit
                    ),
                )
            }
        BatteryLineChart(
            points = chartPoints,
            windowEndMillis = oneMinuteChartWindowEnd(
                chartPoints,
                uiState.latestSampleTimestampMillis
            ),
            windowMillis = TEMPERATURE_CHART_WINDOW_MILLIS,
            xLabels = secondsAxisLabels(),
            axisBounds = temperatureAxisBounds(uiState.temperatureUnit),
            valueLabelFormatter = {
                String.format(
                    Locale.US,
                    "%.1f\u00B0%s",
                    it,
                    uiState.temperatureUnit
                )
            },
        )
    }
}

@Composable
private fun BatteryElectricCurrentCard(
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

@Composable
private fun BatteryChartCard(
    title: String,
    rows: List<Pair<String, String>>,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
        ) {
            BatterySectionHeader(title)
            Spacer(modifier = Modifier.height(20.dp))
            rows.forEachIndexed { index, (label, value) ->
                InfoRow(label, value)
                if (index != rows.lastIndex) {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

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

@Composable
private fun BatteryLineChart(
    points: List<ChartPoint>,
    windowEndMillis: Long,
    windowMillis: Long,
    xLabels: List<String>,
    axisBounds: ChartAxisBounds,
    valueLabelFormatter: (Float) -> String,
    maxPoints: Int? = null,
) {
    val chartPrimary = BatteryGreen

    Canvas(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(159.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF9FAFE)),
    ) {
        val chartLeft = 28.dp.toPx()
        val chartRight = size.width - 52.dp.toPx()
        val chartTop = 16.dp.toPx()
        val chartBottom = size.height - 24.dp.toPx()
        val dash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f)
        val textPaint = chartTextPaint(ChartGrid, 9.dp.toPx())
        val valuePaint = chartTextPaint(chartPrimary, 9.dp.toPx(), Typeface.DEFAULT_BOLD)

        repeat(6) { index ->
            val fraction = index / 5f
            val y = chartTop + (chartBottom - chartTop) * fraction
            drawLine(
                color = ChartGrid,
                start = Offset(chartLeft, y),
                end = Offset(chartRight, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dash,
            )
            drawContext.canvas.nativeCanvas.drawText(
                axisBounds.yLabels.getOrNull(index).orEmpty(),
                chartRight + 8.dp.toPx(),
                y + 3.dp.toPx(),
                textPaint,
            )
        }

        xLabels.forEachIndexed { index, label ->
            val x =
                if (xLabels.size == 1) {
                    chartRight
                } else {
                    chartLeft + (chartRight - chartLeft) * index / (xLabels.size - 1).toFloat()
                }
            drawLine(
                color = ChartGrid,
                start = Offset(x, chartTop),
                end = Offset(x, chartBottom),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dash,
            )
            textPaint.textAlign = Paint.Align.CENTER
            drawContext.canvas.nativeCanvas.drawText(
                label,
                x,
                chartBottom + 14.dp.toPx(),
                textPaint
            )
            textPaint.textAlign = Paint.Align.LEFT
        }

        if (windowEndMillis <= 0L) return@Canvas
        val visiblePoints =
            points
                .asSequence()
                .filter { it.timestampMillis >= windowEndMillis - windowMillis }
                .filter { it.timestampMillis <= windowEndMillis }
                .sortedBy { it.timestampMillis }
                .toList()
        if (visiblePoints.isEmpty()) return@Canvas

        val widthBasedMaxVisiblePoints = max(2, ((chartRight - chartLeft) / 4.dp.toPx()).toInt())
        val maxVisiblePoints = maxPoints?.coerceAtLeast(2) ?: widthBasedMaxVisiblePoints
        val drawablePoints = downsampleChartPoints(visiblePoints, maxVisiblePoints)
        val offsets =
            drawablePoints.map {
                it.toOffset(
                    windowEndMillis = windowEndMillis,
                    windowMillis = windowMillis,
                    axisBounds = axisBounds,
                    chartLeft = chartLeft,
                    chartRight = chartRight,
                    chartTop = chartTop,
                    chartBottom = chartBottom,
                )
            }

        if (offsets.size == 1) {
            drawCircle(
                color = chartPrimary,
                radius = 3.dp.toPx(),
                center = offsets.first(),
            )
        } else {
            val fillPath =
                Path().apply {
                    moveTo(offsets.first().x, chartBottom)
                    offsets.forEach { lineTo(it.x, it.y) }
                    lineTo(offsets.last().x, chartBottom)
                    close()
                }
            drawPath(fillPath, color = ChartFill)
            offsets.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = chartPrimary,
                    start = start,
                    end = end,
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }

        val minPoint = visiblePoints.minByOrNull { it.value }
        val maxPoint = visiblePoints.maxByOrNull { it.value }
        maxPoint?.let { point ->
            drawChartValueLabel(
                label = valueLabelFormatter(point.value),
                anchor =
                    point.toOffset(
                        windowEndMillis,
                        windowMillis,
                        axisBounds,
                        chartLeft,
                        chartRight,
                        chartTop,
                        chartBottom,
                    ),
                paint = valuePaint,
                chartLeft = chartLeft,
                chartRight = chartRight,
                above = true,
            )
        }
        if (minPoint != null && minPoint != maxPoint) {
            drawChartValueLabel(
                label = valueLabelFormatter(minPoint.value),
                anchor =
                    minPoint.toOffset(
                        windowEndMillis,
                        windowMillis,
                        axisBounds,
                        chartLeft,
                        chartRight,
                        chartTop,
                        chartBottom,
                    ),
                paint = valuePaint,
                chartLeft = chartLeft,
                chartRight = chartRight,
                above = false,
            )
        }
    }
}

@Composable
private fun BatterySectionHeader(text: String) {
    Column {
        Text(
            text = text,
            color = BatteryText,
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
                    .background(BatteryDivider),
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
            color = BatteryText,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = BatteryText,
            fontSize = 18.sp,
            lineHeight = 21.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

private typealias ChartPoint = BatteryChartPoint
private typealias ChartAxisBounds = BatteryChartAxisBounds

private fun secondsAxisLabels(): List<String> =
    batteryCurrentAxisLabels(BatteryCurrentRange.OneMinute)

private fun currentAxisLabels(range: BatteryCurrentRange): List<String> =
    batteryCurrentAxisLabels(range)

private fun currentChartMaxPoints(range: BatteryCurrentRange): Int? =
    batteryCurrentChartMaxPoints(range)

private fun currentChartPoints(
    samples: List<BatteryCurrentSample>,
    selectedRange: BatteryCurrentRange,
    windowEndMillis: Long,
): List<ChartPoint> = batteryCurrentChartPoints(samples, selectedRange, windowEndMillis)

private fun currentChartWindowEndMillis(
    samples: List<BatteryCurrentSample>,
    selectedRange: BatteryCurrentRange,
    latestSampleTimestampMillis: Long,
): Long = batteryCurrentChartWindowEndMillis(samples, selectedRange, latestSampleTimestampMillis)

private fun oneMinuteChartWindowEnd(
    points: List<ChartPoint>,
    latestSampleTimestampMillis: Long,
): Long = batteryOneMinuteWindowEnd(points, latestSampleTimestampMillis)

private fun temperatureAxisBounds(unit: String): ChartAxisBounds =
    batteryTemperatureAxisBounds(unit)

private fun currentAxisBounds(values: List<Float>): ChartAxisBounds =
    batteryCurrentAxisBounds(values)

private fun ChartPoint.toOffset(
    windowEndMillis: Long,
    windowMillis: Long,
    axisBounds: ChartAxisBounds,
    chartLeft: Float,
    chartRight: Float,
    chartTop: Float,
    chartBottom: Float,
): Offset {
    val elapsed = (windowEndMillis - timestampMillis).coerceIn(0L, windowMillis)
    val xFraction = 1f - elapsed / windowMillis.toFloat()
    val yFraction = ((value - axisBounds.min) / axisBounds.range).coerceIn(0f, 1f)
    return Offset(
        x = chartLeft + (chartRight - chartLeft) * xFraction,
        y = chartBottom - (chartBottom - chartTop) * yFraction,
    )
}

private fun downsampleChartPoints(
    points: List<ChartPoint>,
    maxPoints: Int,
): List<ChartPoint> = downsampleBatteryChartPoints(points, maxPoints)

private fun chartTextPaint(
    color: Color,
    textSizePx: Float,
    typeface: Typeface = Typeface.DEFAULT,
): Paint =
    Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color.toArgb()
        textSize = textSizePx
        this.typeface = typeface
        textAlign = Paint.Align.LEFT
    }

private fun DrawScope.drawChartValueLabel(
    label: String,
    anchor: Offset,
    paint: Paint,
    chartLeft: Float,
    chartRight: Float,
    above: Boolean,
) {
    val labelWidth = paint.measureText(label)
    val x = (anchor.x + 4.dp.toPx()).coerceIn(chartLeft, chartRight - labelWidth)
    val yOffset = if (above) -5.dp.toPx() else 12.dp.toPx()
    val y = (anchor.y + yOffset).coerceIn(11.dp.toPx(), size.height - 8.dp.toPx())
    drawContext.canvas.nativeCanvas.drawText(label, x, y, paint)
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

private const val TEMPERATURE_CHART_WINDOW_MILLIS = 60_000L
