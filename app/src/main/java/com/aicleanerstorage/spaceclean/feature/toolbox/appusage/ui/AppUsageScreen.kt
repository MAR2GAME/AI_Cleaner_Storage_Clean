package com.aicleanerstorage.spaceclean.feature.toolbox.appusage.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.common.ui.components.PackageAppIcon
import com.aicleanerstorage.spaceclean.common.ui.components.RoundedProgressBar
import com.aicleanerstorage.spaceclean.common.ui.components.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.AppUsageAction
import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.AppUsageDateRange
import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.AppUsageDisplayItem
import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.AppUsageInfo
import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.AppUsageMetricTab
import com.aicleanerstorage.spaceclean.feature.toolbox.appusage.AppUsageUiState
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.ui.ToolboxScanningContent
import kotlinx.coroutines.launch
import java.util.Locale

private val CardBg = Color.White
private val PageBg = Color(0xFFF4F4F4)
private val Navy = Color(0xFF282927)
private val NavyMuted = Color(0xFF999999)
private val PrimaryGreen = Color(0xFF5DD595)
private val StopRed = Color(0xFFFC4141)
private val StopDisabledOrange = Color(0xFFFC6A41)
private val CardRadius = 12.dp
private val OtherColor = Color(0xFF4F75FE)
private val DonutColors = listOf(
    Color(0xFFFF565D),
    Color(0xFFA03CFE),
    Color(0xFF80F17D),
)

@Composable
internal fun AppUsageScreen(
    state: AppUsageUiState,
    onAction: (AppUsageAction) -> Unit,
) {
    var rangeExpanded by remember { mutableStateOf(false) }

    CleanXScaffoldPage(
        title = stringResource(R.string.app_usage),
        onBack = { onAction(AppUsageAction.Back) },
        actions = {
            if (state.hasAccess && !state.isScanning) {
                AppUsageRangeAction(
                    selectedRange = state.selectedRange,
                    expanded = rangeExpanded,
                    onExpandedChange = { rangeExpanded = it },
                    onRangeSelected = { onAction(AppUsageAction.RangeSelected(it)) },
                )
            }
        },
        backgroundBrush = Brush.linearGradient(
            colors = listOf(PageBg, PageBg),
        ),
        contentPadding =
            if (state.hasAccess && state.isScanning) {
                PaddingValues(0.dp)
            } else {
                PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            },
        scrollEnabled = !(state.hasAccess && state.isScanning),
    ) {
        if (state.hasAccess && state.isScanning) {
            ToolboxScanningContent(
                centerIconRes = R.drawable.ic_scan_app_usage,
                captionText = stringResource(R.string.app_usage),
            )
        } else if (state.hasAccess) {
            Spacer(modifier = Modifier.height(12.dp))

            PieChartCard(uiState = state)

            Spacer(modifier = Modifier.height(16.dp))

            UsageDataCard(
                uiState = state,
                onTabSelected = { onAction(AppUsageAction.TabSelected(it)) },
                onStopApp = { onAction(AppUsageAction.StopApp(it)) },
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    if (state.showStopDialog) {
        StopScanDialog(
            onQuit = { onAction(AppUsageAction.QuitScan) },
            onResume = { onAction(AppUsageAction.ResumeScan) },
        )
    }
}

@Composable
private fun AppUsageRangeAction(
    selectedRange: AppUsageDateRange,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onRangeSelected: (AppUsageDateRange) -> Unit,
) {
    Box {
        Surface(
            modifier =
                Modifier
                    .widthIn(max = 154.dp)
                    .height(32.dp)
                    .clickable { onExpandedChange(!expanded) },
            color = PrimaryGreen,
            shape = RoundedCornerShape(4.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(selectedRange.labelRes),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.White,
                    modifier =
                        Modifier
                            .size(24.dp)
                            .rotate(if (expanded) 180f else 0f),
                )
            }
        }
        AppUsageRangeMenu(
            expanded = expanded,
            selectedRange = selectedRange,
            onDismiss = { onExpandedChange(false) },
            onRangeSelected = { range ->
                onRangeSelected(range)
                onExpandedChange(false)
            },
        )
    }
}

@Composable
private fun AppUsageRangeMenu(
    expanded: Boolean,
    selectedRange: AppUsageDateRange,
    onDismiss: () -> Unit,
    onRangeSelected: (AppUsageDateRange) -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = DpOffset((-96).dp, 4.dp),
        modifier =
            Modifier
                .width(188.dp)
                .height(188.dp)
                .background(CardBg),
        shape = RoundedCornerShape(12.dp),
        containerColor = CardBg,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .width(188.dp)
                    .height(188.dp)
                    .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            AppUsageDateRange.entries.forEachIndexed { index, range ->
                AppUsageRangeMenuItem(
                    range = range,
                    selected = range == selectedRange,
                    onClick = { onRangeSelected(range) },
                )
                if (index != AppUsageDateRange.entries.lastIndex) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFD9DEE6)),
                    )
                }
            }
        }
    }
}

@Composable
private fun AppUsageRangeMenuItem(
    range: AppUsageDateRange,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(21.dp)
                .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(range.labelRes),
            color = Navy,
            fontSize = 18.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(24.dp),
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun PieChartCard(uiState: AppUsageUiState) {
    val allUsages = uiState.usages
    val sortedUsages =
        allUsages.sortedWith(
            compareByDescending<AppUsageInfo> { it.totalForegroundMs }
                .thenBy { it.appName.lowercase(Locale.US) }
                .thenBy { it.packageName },
        )

    val topItems = sortedUsages.take(3)
    val otherItems = sortedUsages.drop(3)

    val donutItems = mutableListOf<DonutItem>()
    topItems.forEachIndexed { index, usage ->
        val fraction = if (uiState.totalUsageMs > 0) {
            usage.totalForegroundMs.toFloat() / uiState.totalUsageMs
        } else {
            0f
        }
        donutItems.add(
            DonutItem(
                fraction = fraction.coerceAtLeast(0.02f),
                color = donutColor(index),
            ),
        )
    }
    if (otherItems.isNotEmpty()) {
        val otherTotalMs = otherItems.sumOf { it.totalForegroundMs }
        val otherFraction = if (uiState.totalUsageMs > 0) {
            otherTotalMs.toFloat() / uiState.totalUsageMs
        } else {
            0f
        }
        donutItems.add(
            DonutItem(
                fraction = otherFraction.coerceAtLeast(0.02f),
                color = OtherColor,
            ),
        )
    }

    if (donutItems.isEmpty()) {
        donutItems.add(DonutItem(1f, NavyMuted.copy(alpha = 0.2f)))
    }

    val legendItems =
        when {
            allUsages.isEmpty() ->
                listOf(
                    LegendEntry(
                        color = NavyMuted.copy(alpha = 0.3f),
                        label = stringResource(R.string.no_usage_data_available),
                    ),
                )
            else ->
                buildList {
                    topItems.forEachIndexed { index, usage ->
                        add(
                            LegendEntry(
                                color = donutColor(index),
                                label = usage.appName,
                            ),
                        )
                    }
                    if (otherItems.isNotEmpty()) {
                        add(
                            LegendEntry(
                                color = OtherColor,
                                label = stringResource(R.string.other),
                            ),
                        )
                    }
                }
        }

    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(170.dp),
        color = CardBg,
        shape = RoundedCornerShape(12.dp),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val compact = maxWidth < 330.dp
            val startPadding = if (compact) 20.dp else 35.dp
            val legendGap = if (compact) 24.dp else 44.dp

            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(start = startPadding, end = 16.dp, top = 20.dp, bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(117.dp),
                    verticalArrangement =
                        if (legendItems.size > 1) {
                            Arrangement.SpaceBetween
                        } else {
                            Arrangement.Center
                        },
                ) {
                    legendItems.forEach { item ->
                        LegendItem(
                            color = item.color,
                            label = item.label,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(legendGap))

                Box(
                    modifier = Modifier.size(144.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    DonutChart(
                        modifier = Modifier.size(144.dp),
                        items = donutItems,
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formatDurationForCenter(uiState.totalUsageMs),
                            color = Navy,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                        )
                    }
                }

            }
        }
    }
}

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
            modifier = Modifier
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

@Composable
private fun UsageDataCard(
    uiState: AppUsageUiState,
    onTabSelected: (AppUsageMetricTab) -> Unit,
    onStopApp: (String) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = if (uiState.selectedTab == AppUsageMetricTab.Duration) 0 else 1,
        pageCount = { 2 },
    )
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        val newTab =
            if (pagerState.currentPage == 0) {
                AppUsageMetricTab.Duration
            } else {
                AppUsageMetricTab.LaunchCount
            }
        if (newTab != uiState.selectedTab) {
            onTabSelected(newTab)
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBg,
        shape = RoundedCornerShape(CardRadius),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AppUsageHeaderTab(
                    selected = uiState.selectedTab == AppUsageMetricTab.Duration,
                    title = stringResource(AppUsageMetricTab.Duration.titleRes),
                ) {
                    if (pagerState.currentPage != 0) {
                        coroutineScope.launch { pagerState.animateScrollToPage(0) }
                    }
                }
                AppUsageHeaderTab(
                    selected = uiState.selectedTab == AppUsageMetricTab.LaunchCount,
                    title = stringResource(AppUsageMetricTab.LaunchCount.titleRes),
                ) {
                    if (pagerState.currentPage != 1) {
                        coroutineScope.launch { pagerState.animateScrollToPage(1) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uiState.visibleItems.isEmpty()) {
                    EmptyUsageText()
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                    ) {
                        uiState.visibleItems.forEach { item ->
                            key(item.packageName) {
                                AppUsageRow(
                                    item = item,
                                    selectedTab = uiState.selectedTab,
                                    onStopApp = onStopApp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyUsageText() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.no_usage_data_available),
            color = NavyMuted,
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun AppUsageHeaderTab(
    selected: Boolean,
    title: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) PrimaryGreen else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = title,
            color = if (selected) Color.White else Navy,
            fontSize = 20.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun AppUsageRow(
    item: AppUsageDisplayItem,
    selectedTab: AppUsageMetricTab,
    onStopApp: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PackageAppIcon(
            packageName = item.packageName,
            fallbackText = item.iconText.take(1).ifBlank { "A" },
            color = donutColor(item.colorIndex),
            modifier = Modifier.size(44.dp),
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.appName,
                color = Navy,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(12.dp))
            RoundedProgressBar(
                progress = item.progress,
                width = 185.dp,
                height = 4.dp,
                trackColor = Navy.copy(alpha = 0.15f),
                fillColor = PrimaryGreen,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = itemValueText(item, selectedTab),
                color = NavyMuted,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Button(
            onClick = { onStopApp(item.packageName) },
            enabled = item.isRunning,
            modifier = Modifier
                .width(64.dp)
                .height(32.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StopRed,
                disabledContainerColor = StopDisabledOrange.copy(alpha = 0.65f),
                contentColor = Color.White,
                disabledContentColor = Color.White,
            ),
        ) {
            Text(
                text = stringResource(R.string.stop),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun itemValueText(item: AppUsageDisplayItem, selectedTab: AppUsageMetricTab): String =
    when (selectedTab) {
        AppUsageMetricTab.Duration -> formatDuration(item.totalForegroundMs)
        AppUsageMetricTab.LaunchCount -> pluralStringResource(
            R.plurals.launch_times_count,
            item.launchCount,
            item.launchCount,
        )
    }

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
        val topLeft = Offset(
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

private fun donutColor(index: Int): Color = DonutColors[index % DonutColors.size]


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

@Composable
private fun formatDuration(totalMs: Long): String {
    val totalMinutes = (totalMs / 60_000L).coerceAtLeast(0L)
    val hours = totalMinutes / 60L
    val minutes = totalMinutes % 60L
    return when {
        hours > 0L && minutes > 0L -> stringResource(R.string.duration_hours_minutes, hours, minutes)
        hours > 0L -> stringResource(R.string.duration_hours, hours)
        else -> stringResource(R.string.duration_minutes, minutes)
    }
}
