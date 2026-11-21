package com.aicleanerstorage.spaceclean.app.view.appusage.ui.part

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageDisplayItem
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageMetricTab
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageUiState
import com.aicleanerstorage.spaceclean.base.designsystem.component.PackageAppIcon
import com.aicleanerstorage.spaceclean.base.designsystem.component.RoundedProgressBar
import kotlinx.coroutines.launch

@Composable
internal fun UsageDataCard(
    uiState: AppUsageUiState,
    onTabSelected: (AppUsageMetricTab) -> Unit,
    onStopApp: (String) -> Unit,
) {
    val pagerState =
        rememberPagerState(
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
            modifier =
                Modifier
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

private val StopRed = Color(0xFFFC4141)

private val StopDisabledOrange = Color(0xFFFC6A41)

private val CardRadius = 12.dp

@Composable
private fun EmptyUsageText() {
    Box(
        modifier =
            Modifier
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
        modifier =
            Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (selected) PrimaryGreen else Color.Transparent)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ).padding(horizontal = 14.dp, vertical = 10.dp),
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
            modifier =
                Modifier
                    .width(64.dp)
                    .height(32.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(8.dp),
            colors =
                ButtonDefaults.buttonColors(
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
private fun itemValueText(
    item: AppUsageDisplayItem,
    selectedTab: AppUsageMetricTab,
): String =
    when (selectedTab) {
        AppUsageMetricTab.Duration -> formatDuration(item.totalForegroundMs)
        AppUsageMetricTab.LaunchCount ->
            pluralStringResource(
                R.plurals.launch_times_count,
                item.launchCount,
                item.launchCount,
            )
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
