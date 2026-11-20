package com.aicleanerstorage.spaceclean.app.view.settings.ui.part

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.app.view.common.settings.SettingsRow
import com.aicleanerstorage.spaceclean.app.view.common.settings.settingsTextColor

@Composable
internal fun SettingsNavigationRow(
    label: String,
    onClick: () -> Unit,
) {
    SettingsRow(
        label = label,
        onClick = onClick,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = settingsTextColor,
        )
    }
}
