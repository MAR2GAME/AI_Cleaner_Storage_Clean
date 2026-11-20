package com.aicleanerstorage.spaceclean.app.view.settings.ui.part

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.app.view.common.settings.SettingsRow
import com.aicleanerstorage.spaceclean.app.view.common.settings.settingsTextColor

@Composable
internal fun SettingsValueRow(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    SettingsRow(
        label = label,
        onClick = onClick,
    ) {
        Text(
            text = value,
            color = settingsTextColor,
            fontSize = 16.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
        )
    }
}
