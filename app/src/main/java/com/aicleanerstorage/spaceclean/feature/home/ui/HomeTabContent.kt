package com.aicleanerstorage.spaceclean.feature.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.ui.components.CleanXPillShape
import com.aicleanerstorage.spaceclean.feature.home.HomeSummaryUiState
import com.aicleanerstorage.spaceclean.feature.toolbox.shared.device.model.StorageInfo

private const val STORAGE_CARD_HEIGHT = 151
private const val FEATURE_CARD_HEIGHT = 199

private val HomeText = Color(0xFF282927)
private val HomeMutedText = Color(0xFF999999)
private val StorageTrack = Color(0xFFEEF4F9)

private data class StorageCardStyle(
    val accent: Color,
)

private val StorageLowStyle = StorageCardStyle(Color(0xFF5DD595))
private val StorageMediumStyle = StorageCardStyle(Color(0xFFF9B82B))
private val StorageHighStyle = StorageCardStyle(Color(0xFFF9582B))

@Composable
fun HomeTabContent(
    summaryState: HomeSummaryUiState,
    onFeatureClick: (FeatureKey) -> Unit = {},
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
    ) {
        StorageCard(
            storageInfo = summaryState.storageInfo,
            onClick = { onFeatureClick(FeatureKey.JUNK_CLEAN) },
        )
        Spacer(modifier = Modifier.height(32.dp))

        HomeFeatureCard(
            title = stringResource(R.string.home_virus_title),
            description = stringResource(R.string.home_virus_desc),
            gradient = Brush.horizontalGradient(listOf(Color(0xFF1793FF), Color(0xFF84DEFE))),
            artworkRes = R.drawable.home_virus_figma_cropped,
            artworkOnStart = true,
            onClick = { onFeatureClick(FeatureKey.ANTI_VIRUS) },
        )
        Spacer(modifier = Modifier.height(32.dp))

        HomeFeatureCard(
            title =
                pluralStringResource(
                    R.plurals.locked_apps_count,
                    summaryState.lockedAppCount,
                    summaryState.lockedAppCount,
                ),
            description = stringResource(R.string.home_locked_apps_desc),
            gradient = Brush.horizontalGradient(listOf(Color(0xFF535FFE), Color(0xFF9EA9FE))),
            artworkRes = R.drawable.home_app_lock_figma,
            artworkOnStart = false,
            onClick = { onFeatureClick(FeatureKey.APP_LOCK) },
        )
    }
}

@Composable
private fun StorageCard(
    storageInfo: StorageInfo,
    onClick: () -> Unit,
) {
    val style = storageCardStyle(storageInfo.usagePercent)

    Surface(
        modifier = Modifier.fillMaxWidth().height(STORAGE_CARD_HEIGHT.dp),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StorageCardBody(storageInfo = storageInfo, style = style, onClick = onClick)
            Spacer(modifier = Modifier.width(4.dp))
            Image(
                painter = painterResource(R.drawable.trash_can),
                contentDescription = null,
                modifier = Modifier.size(width = 103.dp, height = 119.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun StorageCardBody(
    storageInfo: StorageInfo,
    style: StorageCardStyle,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.width(216.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StorageMeta(storageInfo = storageInfo)
            StorageProgress(
                usagePercent = storageInfo.usagePercent,
                accent = style.accent,
            )
        }
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(40.dp),
            shape = CleanXPillShape,
            colors = ButtonDefaults.buttonColors(containerColor = style.accent, contentColor = Color.White),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.home_remove_junk),
                fontSize = 16.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun StorageMeta(storageInfo: StorageInfo) {
    val usedText = storageInfo.formattedUsed.withoutSpaces()
    val totalText = "/ ${storageInfo.formattedTotal.withoutSpaces()}"
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val totalTextStyle =
        TextStyle(
            color = HomeMutedText,
            fontSize = 14.sp,
            lineHeight = 18.sp,
        )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
    ) {
        Text(
            text = stringResource(R.string.home_storage_label),
            color = HomeText,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.widthIn(max = 68.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.width(10.dp))
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            val maxWidthPx = with(density) { maxWidth.roundToPx() }
            val measuredTotalWidthPx =
                remember(totalText, textMeasurer) {
                    textMeasurer.measure(
                        text = totalText,
                        style = totalTextStyle,
                        maxLines = 1,
                    ).size.width
                }
            val totalWidthPx = measuredTotalWidthPx.coerceAtMost(maxWidthPx)
            val usedWidthPx = (maxWidthPx - totalWidthPx).coerceAtLeast(0)
            val usedFontSize =
                remember(usedText, usedWidthPx, textMeasurer) {
                    (24 downTo 16).firstOrNull { size ->
                        textMeasurer.measure(
                            text = usedText,
                            style =
                                TextStyle(
                                    fontSize = size.sp,
                                    fontWeight = FontWeight.Bold,
                                ),
                            maxLines = 1,
                        ).size.width <= usedWidthPx
                    }?.sp ?: 16.sp
                }
            val totalWidth = with(density) { totalWidthPx.toDp() }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = usedText,
                    color = HomeText,
                    fontSize = usedFontSize,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = totalText,
                    style = totalTextStyle,
                    modifier = Modifier.width(totalWidth).padding(bottom = 2.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun StorageProgress(
    usagePercent: Int,
    accent: Color,
) {
    val fraction = usagePercent.coerceIn(0, 100) / 100f
    Box(modifier = Modifier.fillMaxWidth().height(16.dp)) {
        ProgressFill(
            modifier = Modifier.fillMaxWidth(),
            color = StorageTrack,
        )
        ProgressFill(
            modifier = Modifier.fillMaxWidth(fraction),
            color = accent,
        )
    }
}

@Composable
private fun ProgressFill(
    modifier: Modifier,
    color: Color,
) {
    Box(
        modifier =
            modifier
                .padding(top = 4.dp)
                .height(12.dp)
                .clip(CleanXPillShape)
                .background(color),
    )
}

@Composable
private fun HomeFeatureCard(
    title: String,
    description: String,
    gradient: Brush,
    artworkRes: Int,
    artworkOnStart: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(FEATURE_CARD_HEIGHT.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(gradient)
                .clickable(onClick = onClick),
    ) {
        FeatureCardBody(
            title = title,
            description = description,
            artworkRes = artworkRes,
            artworkOnStart = artworkOnStart,
        )
        Button(
            onClick = onClick,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxWidth()
                    .height(46.dp),
            shape = CleanXPillShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = HomeText),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            Text(
                text = stringResource(R.string.clean_now),
                color = HomeText,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun BoxScope.FeatureCardBody(
    title: String,
    description: String,
    artworkRes: Int,
    artworkOnStart: Boolean,
) {
    if (artworkOnStart) {
        Image(
            painter = painterResource(artworkRes),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopStart).size(width = 122.dp, height = 130.dp),
            contentScale = ContentScale.Fit,
        )
        FeatureCardCopy(
            title = title,
            description = description,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 24.dp, end = 10.dp).width(222.dp),
            alignEnd = true,
        )
    } else {
        FeatureCardCopy(
            title = title,
            description = description,
            modifier = Modifier.align(Alignment.TopStart).padding(start = 10.dp, top = 24.dp).width(210.dp),
            alignEnd = false,
        )
        Image(
            painter = painterResource(artworkRes),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd).size(width = 122.dp, height = 124.dp),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun FeatureCardCopy(
    title: String,
    description: String,
    modifier: Modifier,
    alignEnd: Boolean,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 28.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
        )
        Text(
            text = description,
            color = Color.White,
            fontSize = 18.sp,
            lineHeight = 21.sp,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
        )
    }
}

private fun storageCardStyle(usagePercent: Int): StorageCardStyle {
    val ratio = usagePercent.coerceIn(0, 100) / 100f
    return when {
        ratio < 1f / 3f -> StorageLowStyle
        ratio <= 2f / 3f -> StorageMediumStyle
        else -> StorageHighStyle
    }
}

private fun String.withoutSpaces(): String = replace(" ", "")
