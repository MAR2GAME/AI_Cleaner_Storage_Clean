package com.aicleanerstorage.spaceclean.app.view.battery.ui.part

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.battery.interaction.BatteryChartAxisBounds
import com.aicleanerstorage.spaceclean.app.view.battery.interaction.BatteryChartPoint
import com.aicleanerstorage.spaceclean.app.view.battery.interaction.batteryCurrentAxisLabels
import com.aicleanerstorage.spaceclean.app.view.battery.interaction.batteryOneMinuteWindowEnd
import com.aicleanerstorage.spaceclean.app.view.battery.interaction.batteryTemperatureAxisBounds
import com.aicleanerstorage.spaceclean.app.view.battery.interaction.downsampleBatteryChartPoints
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.BatteryCurrentRange
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.DeviceInfoUiState
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.displayBatteryTemperatureValue
import com.aicleanerstorage.spaceclean.app.view.deviceinfo.interaction.formatBatteryTemperature
import java.util.Locale
import kotlin.math.max

@Composable
internal fun BatteryTemperatureCard(uiState: DeviceInfoUiState) {
    BatteryChartCard(
        title = stringResource(R.string.battery_temperature_title),
        rows =
            listOf(
                stringResource(R.string.battery_realtime_temperature) to
                    formatBatteryTemperature(
                        uiState.displayTemperatureC,
                        uiState.temperatureUnit,
                        includeSpace = false,
                    ),
                stringResource(R.string.battery_average_temperature) to
                    formatBatteryTemperature(
                        uiState.selectedTemperatureAverageC,
                        uiState.temperatureUnit,
                        includeSpace = false,
                    ),
            ),
    ) {
        TemperatureChart(uiState)
    }
}

@Composable
private fun TemperatureChart(uiState: DeviceInfoUiState) {
    val chartPoints =
        uiState.selectedTemperatureSamples.map {
            BatteryChartPoint(
                timestampMillis = it.timestampMillis,
                value = displayBatteryTemperatureValue(it.temperatureC, uiState.temperatureUnit),
            )
        }
    BatteryLineChart(
        points = chartPoints,
        windowEndMillis = oneMinuteChartWindowEnd(chartPoints, uiState.latestSampleTimestampMillis),
        windowMillis = TEMPERATURE_CHART_WINDOW_MILLIS,
        xLabels = secondsAxisLabels(),
        axisBounds = temperatureAxisBounds(uiState.temperatureUnit),
        valueLabelFormatter = {
            String.format(Locale.US, "%.1f\u00B0%s", it, uiState.temperatureUnit)
        },
    )
}

internal val BatteryGreen = Color(0xFF5DD595)

private val ChartGrid = Color(0xFF9AA6B8).copy(alpha = 0.72f)

private val ChartFill = BatteryGreen.copy(alpha = 0.14f)

@Composable
internal fun BatteryChartCard(
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
internal fun BatteryLineChart(
    points: List<BatteryChartPoint>,
    windowEndMillis: Long,
    windowMillis: Long,
    xLabels: List<String>,
    axisBounds: BatteryChartAxisBounds,
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
                textPaint,
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

private fun secondsAxisLabels(): List<String> = batteryCurrentAxisLabels(BatteryCurrentRange.OneMinute)

private fun oneMinuteChartWindowEnd(
    points: List<BatteryChartPoint>,
    latestSampleTimestampMillis: Long,
): Long = batteryOneMinuteWindowEnd(points, latestSampleTimestampMillis)

private fun temperatureAxisBounds(unit: String): BatteryChartAxisBounds = batteryTemperatureAxisBounds(unit)

private fun BatteryChartPoint.toOffset(
    windowEndMillis: Long,
    windowMillis: Long,
    axisBounds: BatteryChartAxisBounds,
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
    points: List<BatteryChartPoint>,
    maxPoints: Int,
): List<BatteryChartPoint> = downsampleBatteryChartPoints(points, maxPoints)

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

private const val TEMPERATURE_CHART_WINDOW_MILLIS = 60_000L

