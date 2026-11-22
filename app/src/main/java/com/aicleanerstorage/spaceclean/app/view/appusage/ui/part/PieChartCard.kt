package com.aicleanerstorage.spaceclean.app.view.appusage.ui.part

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageUiState
import com.aicleanerstorage.spaceclean.domain.model.appusage.AppUsageInfoModel
import java.util.Locale
@Composable
internal fun PieChartCard(uiState: AppUsageUiState) {
    val chart =
        buildChartData(
            uiState = uiState,
            emptyLabel = stringResource(R.string.no_usage_data_available),
            otherLabel = stringResource(R.string.other),
        )
    PieChartSurface(
        totalUsageMs = uiState.totalUsageMs,
        chart = chart,
    )
}

private data class PieChartData(
    val donutItems: List<DonutItem>,
    val legendItems: List<LegendEntry>,
)

private fun buildChartData(
    uiState: AppUsageUiState,
    emptyLabel: String,
    otherLabel: String,
): PieChartData {
    val sortedUsages =
        uiState.usages.sortedWith(
            compareByDescending<AppUsageInfoModel> { it.totalForegroundMs }
                .thenBy { it.appName.lowercase(Locale.US) }
                .thenBy { it.packageName },
        )
    return PieChartData(
        donutItems = buildDonutItems(sortedUsages, uiState.totalUsageMs),
        legendItems = buildLegendEntries(sortedUsages, emptyLabel, otherLabel),
    )
}

private fun buildDonutItems(
    sortedUsages: List<AppUsageInfoModel>,
    totalUsageMs: Long,
): List<DonutItem> {
    val topItems = sortedUsages.take(3)
    val otherItems = sortedUsages.drop(3)
    val items = mutableListOf<DonutItem>()
    topItems.forEachIndexed { index, usage ->
        val fraction = if (totalUsageMs > 0) usage.totalForegroundMs.toFloat() / totalUsageMs else 0f
        items += DonutItem(fraction.coerceAtLeast(0.02f), donutColor(index))
    }
    if (otherItems.isNotEmpty()) {
        val otherFraction =
            if (totalUsageMs > 0) otherItems.sumOf { it.totalForegroundMs }.toFloat() / totalUsageMs else 0f
        items += DonutItem(otherFraction.coerceAtLeast(0.02f), OtherColor)
    }
    if (items.isEmpty()) items += DonutItem(1f, NavyMuted.copy(alpha = 0.2f))
    return items
}

private fun buildLegendEntries(
    sortedUsages: List<AppUsageInfoModel>,
    emptyLabel: String,
    otherLabel: String,
): List<LegendEntry> {
    if (sortedUsages.isEmpty()) {
        return listOf(LegendEntry(NavyMuted.copy(alpha = 0.3f), emptyLabel))
    }
    val topItems = sortedUsages.take(3)
    val otherItems = sortedUsages.drop(3)
    return buildList {
        topItems.forEachIndexed { index, usage -> add(LegendEntry(donutColor(index), usage.appName)) }
        if (otherItems.isNotEmpty()) add(LegendEntry(OtherColor, otherLabel))
    }
}

@Composable
private fun PieChartSurface(
    totalUsageMs: Long,
    chart: PieChartData,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(170.dp),
        color = CardBg,
        shape = RoundedCornerShape(12.dp),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            PieChartRow(
                totalUsageMs = totalUsageMs,
                donutItems = chart.donutItems,
                legendItems = chart.legendItems,
                compact = maxWidth < 330.dp,
            )
        }
    }
}

@Composable
private fun PieChartRow(
    totalUsageMs: Long,
    donutItems: List<DonutItem>,
    legendItems: List<LegendEntry>,
    compact: Boolean,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    start = if (compact) 20.dp else 35.dp,
                    end = 16.dp,
                    top = 20.dp,
                    bottom = 20.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .height(117.dp),
            verticalArrangement = if (legendItems.size > 1) Arrangement.SpaceBetween else Arrangement.Center,
        ) {
            legendItems.forEach { item ->
                LegendItem(color = item.color, label = item.label, modifier = Modifier.fillMaxWidth())
            }
        }
        Spacer(modifier = Modifier.width(if (compact) 24.dp else 44.dp))
        Box(modifier = Modifier.size(144.dp), contentAlignment = Alignment.Center) {
            DonutChart(modifier = Modifier.size(144.dp), items = donutItems)
            Text(
                text = formatDurationForCenter(totalUsageMs),
                color = Navy,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
internal val NavyMuted = Color(0xFF999999)

private val OtherColor = Color(0xFF4F75FE)

private val DonutColors =
    listOf(
        Color(0xFFFF565D),
        Color(0xFFA03CFE),
        Color(0xFF80F17D),
    )

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(color),
        )
        Text(
            text = label,
            color = Navy,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private data class LegendEntry(
    val color: Color,
    val label: String,
)

private data class DonutItem(
    val fraction: Float,
    val color: Color,
)

@Composable
private fun DonutChart(
    modifier: Modifier,
    items: List<DonutItem>,
) {
    val emptyColor = NavyMuted.copy(alpha = 0.2f)

    Canvas(modifier = modifier) {
        val strokeWidth = 21.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val topLeft =
            Offset(
                (size.width - radius * 2) / 2,
                (size.height - radius * 2) / 2,
            )
        val arcSize = Size(radius * 2, radius * 2)
        var startAngle = -90f

        if (items.isEmpty()) {
            drawArc(
                color = emptyColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
            )
            return@Canvas
        }

        items.forEach { item ->
            val sweepAngle = item.fraction * 360f
            drawArc(
                color = item.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
            )
            startAngle += sweepAngle
        }
    }
}

internal fun donutColor(index: Int): Color = DonutColors[index % DonutColors.size]

@Composable
private fun formatDurationForCenter(totalMs: Long): String {
    val totalMinutes = (totalMs / 60_000L).coerceAtLeast(0L)
    val hours = totalMinutes / 60L
    val minutes = totalMinutes % 60L
    return when {
        hours > 0L && minutes > 0L -> "${hours}hr ${minutes}\nmin"
        hours > 0L -> "${hours}hr"
        else -> "${minutes}\nmin"
    }
}


