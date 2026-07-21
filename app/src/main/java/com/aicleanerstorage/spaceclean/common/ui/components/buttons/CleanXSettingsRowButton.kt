package com.aicleanerstorage.spaceclean.common.ui.components.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.common.ui.components.cleanXPressable
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXMutedText
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXRowHeight
import com.aicleanerstorage.spaceclean.common.ui.theme.CleanXText

@Composable
fun CleanXSettingsToggleRow(
    label: String,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CleanXSettingsRowFrame(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text(
            text = label,
            color = CleanXText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 22.sp,
            modifier = Modifier.weight(1f),
        )
        CleanXMiniSwitch(checked = checked)
    }
}

@Composable
fun CleanXSettingsNavigationRow(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes trailingIconRes: Int = R.mipmap.ic_next,
) {
    CleanXSettingsRowFrame(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text(
            text = label,
            color = CleanXText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 22.sp,
            modifier = Modifier.weight(1f),
        )
        if (trailingIconRes == R.mipmap.ic_next) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = CleanXMutedText,
            )
        } else {
            Image(
                painter = painterResource(trailingIconRes),
                contentDescription = null,
                modifier = Modifier.size(15.dp),
            )
        }
    }
}

@Composable
fun CleanXMiniSwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(if (checked) R.drawable.ic_switch_open else R.drawable.ic_switch_close),
        contentDescription = null,
        modifier = modifier.size(24.dp),
    )
}

@Composable
private fun CleanXSettingsRowFrame(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(CleanXRowHeight)
                .cleanXPressable(
                    pressedAlpha = 0.72f,
                    pressedScale = 1f,
                    onClick = onClick,
                ),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}
