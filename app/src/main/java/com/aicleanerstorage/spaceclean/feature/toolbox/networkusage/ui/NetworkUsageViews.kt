package com.aicleanerstorage.spaceclean.feature.toolbox.networkusage.ui


import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXScaffoldPage
import com.aicleanerstorage.spaceclean.common.ui.components.PackageAppIcon
import com.aicleanerstorage.spaceclean.common.ui.components.RoundedProgressBar
import com.aicleanerstorage.spaceclean.common.ui.components.popups.StopScanDialog
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.ui.ToolboxScanningContent
import com.aicleanerstorage.spaceclean.feature.toolbox.networkusage.NetworkUsageDisplayItem
import com.aicleanerstorage.spaceclean.feature.toolbox.networkusage.NetworkUsageTab
import com.aicleanerstorage.spaceclean.feature.toolbox.networkusage.NetworkUsageUiState
import com.aicleanerstorage.spaceclean.common.format.FileSizeFormatter
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private val DetailPageBackground = Color(0xFFF4F4F4)
private val DefaultPageBackground = Color(0xFFF7FAFD)
private val CardBackground = Color.White
private val PrimaryText = Color(0xFF282927)
private val MutedText = Color(0xFF999999)
private val PrimaryGreen = Color(0xFF5DD595)
private val CardRadius = 12.dp

@Composable
internal fun NetworkUsageScreenState(
    uiState: NetworkUsageUiState,
    showScanning: Boolean,
    showStopDialog: Boolean,
    onBack: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onQuitScan: () -> Unit,
    onResumeScan: () -> Unit,
) {
    val pagerState =
        rememberPagerState(
            initialPage = uiState.selectedIndex,
            pageCount = { NetworkUsageTab.entries.size },
        )
    val scope = rememberCoroutineScope()
    val visualSelectedPage = pagerState.currentPage.coerceIn(0, NetworkUsageTab.entries.lastIndex)
    LaunchedEffect(pagerState, onTabSelected) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                if (page != uiState.selectedIndex) onTabSelected(page)
            }
    }

    CleanXScaffoldPage(
        title = stringResource(R.string.network_usage),
        onBack = onBack,
        backgroundBrush =
            Brush.linearGradient(
                colors =
                    if (uiState.hasAccess && !showScanning) {
                        listOf(DetailPageBackground, DetailPageBackground)
                    } else {
                        listOf(DefaultPageBackground, DefaultPageBackground)
                    },
            ),
        contentPadding =
            if (uiState.hasAccess && showScanning) {
                PaddingValues(0.dp)
            } else {
                PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            },
        scrollEnabled = !(uiState.hasAccess && showScanning),
    ) {
        if (uiState.hasAccess && showScanning) {
            ToolboxScanningContent(
                centerIconRes = R.drawable.ic_scan_app_usage,
                captionText = stringResource(R.string.network_usage),
            )
        } else if (uiState.hasAccess) {
            Spacer(modifier = Modifier.height(22.dp))
            NetworkUsageTabs(
                uiState = uiState,
                selectedIndex = visualSelectedPage,
                onSelected = { index ->
                    if (index != visualSelectedPage) {
                        scope.launch { pagerState.animateScrollToPage(index) }
                    }
                },
            )

            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = 0,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) { page ->
                NetworkUsagePageContent(
                    uiState = uiState,
                    tab = NetworkUsageTab.entries[page],
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    if (showStopDialog) {
        StopScanDialog(
            onQuit = onQuitScan,
            onResume = onResumeScan,
        )
    }
}

@Composable
private fun NetworkUsagePageContent(
    uiState: NetworkUsageUiState,
    tab: NetworkUsageTab,
) {
    when {
        uiState.isLoading && uiState.usage == null -> Unit
        uiState.totalBytes(tab) <= 0L -> EmptyNetworkState()
        else -> {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                NetworkUsageDetailsCard(uiState = uiState, tab = tab)
            }
        }
    }
}

@Composable
private fun NetworkUsageTabs(
    uiState: NetworkUsageUiState,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
) {
    val tabs =
        listOf(
            stringResource(R.string.cellular_today) to formatNetworkBytes(uiState.cellularTotalBytes),
            stringResource(R.string.wifi_today) to formatNetworkBytes(uiState.wifiTotalBytes),
        )

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val tabWidth = minOf(147.dp, maxWidth / 2)
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                tabs.forEachIndexed { index, (title, value) ->
                    NetworkUsageTabItem(
                        title = title,
                        value = value,
                        width = tabWidth,
                        onClick = { onSelected(index) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .align(Alignment.BottomCenter)
                            .background(PrimaryText.copy(alpha = 0.15f)),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    tabs.indices.forEach { index ->
                        Box(
                            modifier = Modifier.width(tabWidth).height(2.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (index == selectedIndex) {
                                Box(
                                    modifier =
                                        Modifier
                                            .width(85.dp)
                                            .height(2.dp)
                                            .background(PrimaryGreen),
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
private fun NetworkUsageTabItem(
    title: String,
    value: String,
    width: Dp,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .width(width)
                .clickable(onClick = onClick)
                .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            color = PrimaryText,
            fontSize = 20.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            color = MutedText,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun EmptyNetworkState() {
    Box(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        Image(
            painter = painterResource(id = R.drawable.network_usage_empty_figma),
            contentDescription = null,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = 268.dp)
                    .height(206.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun NetworkUsageDetailsCard(
    uiState: NetworkUsageUiState,
    tab: NetworkUsageTab,
) {
    Surface(
        modifier =
            Modifier
                .padding(top = 14.dp)
                .widthIn(max = 331.dp)
                .fillMaxWidth(),
        color = CardBackground,
        shape = RoundedCornerShape(CardRadius),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NetworkSummary(uiState = uiState, tab = tab)
            NetworkAppList(items = uiState.displayItems(tab))
        }
    }
}

@Composable
private fun NetworkSummary(
    uiState: NetworkUsageUiState,
    tab: NetworkUsageTab,
) {
    val usage = uiState.usage
    Row(
        modifier = Modifier.fillMaxWidth().height(49.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SummaryColumn(
            value = formatNetworkBytes(uiState.totalBytes(tab)),
            label =
                stringResource(
                    if (usage?.isToday == false) {
                        R.string.total_usage_since_boot
                    } else {
                        R.string.total_usage_today
                    },
                ).replace('\n', ' '),
        )
        SummaryDivider()
        SummaryColumn(
            value = formatNetworkBytes(uiState.rxBytes(tab)),
            label =
                stringResource(
                    if (usage?.isToday == false) {
                        R.string.downloads_since_boot
                    } else {
                        R.string.downloads_today
                    },
                ).replace('\n', ' '),
        )
        SummaryDivider()
        SummaryColumn(
            value = formatNetworkBytes(uiState.txBytes(tab)),
            label =
                stringResource(
                    if (usage?.isToday == false) {
                        R.string.uploads_since_boot
                    } else {
                        R.string.uploads_today
                    },
                ).replace('\n', ' '),
        )
    }
}

@Composable
private fun SummaryDivider() {
    Box(modifier = Modifier.width(1.dp).height(18.dp).background(PrimaryText.copy(alpha = 0.15f)))
}

@Composable
private fun SummaryColumn(
    value: String,
    label: String,
) {
    Column(
        modifier = Modifier.width(83.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = value,
            color = PrimaryText,
            fontSize = 20.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.requiredWidth(89.dp),
        )
        Text(
            text = label,
            color = PrimaryText,
            fontSize = 10.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.requiredWidth(89.dp),
        )
    }
}

@Composable
private fun NetworkAppList(items: List<NetworkUsageDisplayItem>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        val maxUsage = items.maxOfOrNull { it.totalBytes }?.coerceAtLeast(1L) ?: 1L
        items.forEach { item ->
            NetworkAppRow(
                item = item,
                progress = (item.totalBytes.toFloat() / maxUsage).coerceIn(0.08f, 1f),
            )
        }
    }
}

@Composable
private fun NetworkAppRow(
    item: NetworkUsageDisplayItem,
    progress: Float,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PackageAppIcon(
            packageName = item.packageName,
            fallbackText = if (item.isAggregate) "S" else item.appName.take(1).ifBlank { "A" },
            color = PrimaryGreen,
            isAggregate = item.isAggregate,
            modifier = Modifier.size(44.dp),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(44.dp),
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Text(
                    text =
                        if (item.isAggregate) {
                            stringResource(R.string.system_unknown_traffic)
                        } else {
                            item.appName
                        },
                    color = PrimaryText,
                    fontSize = 18.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(196.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.width(209.dp).height(4.dp)) {
                    RoundedProgressBar(
                        progress = progress,
                        width = 190.dp,
                        height = 4.dp,
                        trackColor = PrimaryText.copy(alpha = 0.15f),
                        fillColor = PrimaryGreen,
                    )
                }
            }
            Text(
                text = formatNetworkBytes(item.totalBytes, compact = true),
                color = MutedText,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }
    }
}

private fun formatNetworkBytes(
    bytes: Long,
    compact: Boolean = false,
): String {
    if (bytes <= 0L) return "0 MB"
    val value = FileSizeFormatter.format(bytes.coerceAtLeast(0L))
    return if (compact) value.replace(" ", "") else value
}
