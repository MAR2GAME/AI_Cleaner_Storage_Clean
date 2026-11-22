package com.aicleanerstorage.spaceclean.app.view.onboarding.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.domain.model.device.CapacityInfoModel
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.common.component.StorageValueText
import com.aicleanerstorage.spaceclean.base.util.FormatUtil

@Composable
internal fun OnboardingResultContent(
    rows: List<DeviceScanRow>,
    storageInfo: CapacityInfoModel,
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        StorageCard(storageInfo = storageInfo)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.onboarding_device_info_disclaimer),
            color = OnboardingMuted,
            fontSize = 16.sp,
            lineHeight = 19.sp,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(28.dp))
        DeviceRows(
            rows = rows,
            cardHeight = 377.dp,
        )
        Spacer(modifier = Modifier.height(31.dp))
        GetStartedButton(onClick = onGetStarted)
    }
}

private val OnboardingMuted = Color(0xFF999999)

private val OnboardingProgressTrack = Color(0xFFEEF4F9)

@Composable
private fun StorageCard(storageInfo: CapacityInfoModel) {
    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(115.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
    ) {
        StorageCardRow(storageInfo = storageInfo, compact = maxWidth < 340.dp)
    }
}

@Composable
private fun StorageCardRow(
    storageInfo: CapacityInfoModel,
    compact: Boolean,
) {
    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StorageCardTextColumn(storageInfo = storageInfo, compact = compact)
        Image(
            painter = painterResource(R.drawable.onboarding_trash_can),
            contentDescription = null,
            modifier =
                Modifier.size(
                    width = if (compact) 76.dp else 88.dp,
                    height = if (compact) 65.dp else 75.dp,
                ),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.StorageCardTextColumn(
    storageInfo: CapacityInfoModel,
    compact: Boolean,
) {
    val usedText = compactStorageSize(FormatUtil.formatSize(storageInfo.usedBytes))
    val totalText = " / ${compactStorageSize(FormatUtil.formatSize(storageInfo.totalBytes))}"
    val totalTextStyle =
        TextStyle(
            color = OnboardingMuted,
            fontSize = 16.sp,
            lineHeight = 23.sp,
        )
    Column(
        modifier =
            Modifier
                .weight(1f)
                .padding(end = if (compact) 8.dp else 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.home_storage_label),
                color = OnboardingText,
                fontSize = 16.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.widthIn(max = 68.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.width(8.dp))
            StorageValueText(
                usedText = usedText,
                totalText = totalText,
                totalTextStyle = totalTextStyle,
                color = OnboardingGreen,
            )
        }
        Spacer(modifier = Modifier.height(7.dp))
        StorageProgressBar(storageInfo.usagePercent)
    }
}

@Composable
private fun StorageProgressBar(usagePercent: Int) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(14.5.dp))
                .background(OnboardingProgressTrack),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth((usagePercent.coerceIn(0, 100)) / 100f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(14.5.dp))
                    .background(OnboardingGreen),
        )
    }
}

private fun compactStorageSize(value: String): String = value.replace(" ", "")

@Composable
private fun GetStartedButton(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFFD0FD91), OnboardingGreen),
                    ),
                ).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.onboarding_get_started),
            color = OnboardingText,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}




