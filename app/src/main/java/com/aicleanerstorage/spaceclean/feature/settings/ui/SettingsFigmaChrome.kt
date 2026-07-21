package com.aicleanerstorage.spaceclean.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.cleanXDebouncedClick
import com.aicleanerstorage.spaceclean.common.ui.components.cleanXPressable
import com.aicleanerstorage.spaceclean.common.ui.components.stableStatusBarsPadding

private val SettingsFigmaBackground = Color(0xFFF4F4F4)
private val SettingsFigmaText = Color(0xFF282927)
private val SettingsFigmaDivider = Color(0xFFE2E2E2)
private val SettingsFigmaCardShape = RoundedCornerShape(12.dp)
private val SettingsFigmaHeaderTop = 32.dp
private val SettingsFigmaCardTop = 80.dp
private val SettingsFigmaHorizontalPadding = 16.dp
private val SettingsFigmaRowHeight = 24.dp
private val SettingsFigmaDividerGap = 20.dp

@Composable
internal fun SettingsFigmaScreen(
    title: String,
    onBack: () -> Unit,
    cardMinHeight: Dp,
    modifier: Modifier = Modifier,
    cardContent: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(SettingsFigmaBackground)
                .stableStatusBarsPadding(),
    ) {
        SettingsFigmaTopBar(
            title = title,
            onBack = onBack,
            modifier =
                Modifier
                    .padding(start = SettingsFigmaHorizontalPadding, top = SettingsFigmaHeaderTop)
                    .fillMaxWidth()
                    .height(SettingsFigmaRowHeight),
        )
        Surface(
            modifier =
                Modifier
                    .padding(horizontal = SettingsFigmaHorizontalPadding)
                    .padding(top = SettingsFigmaCardTop)
                    .fillMaxWidth()
                    .heightIn(min = cardMinHeight),
            color = Color.White,
            shape = SettingsFigmaCardShape,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.Top,
                content = cardContent,
            )
        }
    }
}

@Composable
private fun SettingsFigmaTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back),
            modifier =
                Modifier
                    .size(24.dp)
                    .cleanXDebouncedClick(onClick = onBack),
            tint = SettingsFigmaText,
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = title,
            color = SettingsFigmaText,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun SettingsFigmaNavigationRow(
    label: String,
    onClick: () -> Unit,
) {
    SettingsFigmaRow(
        label = label,
        onClick = onClick,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = SettingsFigmaText,
        )
    }
}

@Composable
internal fun SettingsFigmaValueRow(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    SettingsFigmaRow(
        label = label,
        onClick = onClick,
    ) {
        Text(
            text = value,
            color = SettingsFigmaText,
            fontSize = 16.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
        )
    }
}

@Composable
internal fun SettingsFigmaToggleRow(
    label: String,
    checked: Boolean,
    onClick: () -> Unit,
) {
    SettingsFigmaRow(
        label = label,
        onClick = onClick,
    ) {
        Image(
            painter = painterResource(if (checked) R.drawable.ic_switch_open else R.drawable.ic_switch_close),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun SettingsFigmaRow(
    label: String,
    onClick: () -> Unit,
    trailing: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(SettingsFigmaRowHeight)
                .cleanXPressable(
                    pressedAlpha = 0.72f,
                    pressedScale = 1f,
                    onClick = onClick,
                ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = SettingsFigmaText,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        trailing()
    }
}

@Composable
internal fun SettingsFigmaDivider() {
    Spacer(modifier = Modifier.height(SettingsFigmaDividerGap))
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SettingsFigmaDivider),
    )
    Spacer(modifier = Modifier.height(SettingsFigmaDividerGap))
}
