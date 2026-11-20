package com.aicleanerstorage.spaceclean.app.view.common.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.base.designsystem.component.cleanXPressable

@Composable
internal fun SettingsRow(
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
            color = settingsTextColor,
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

private val SettingsFigmaRowHeight = 24.dp

internal val settingsTextColor = Color(0xFF282927)
