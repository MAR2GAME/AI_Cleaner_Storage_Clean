package com.aicleanerstorage.spaceclean.app.view.common.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun SettingsDividerContent() {
    Spacer(modifier = Modifier.height(SettingsFigmaDividerGap))
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(dividerColor),
    )
    Spacer(modifier = Modifier.height(SettingsFigmaDividerGap))
}

private val dividerColor = Color(0xFFE2E2E2)

private val SettingsFigmaDividerGap = 20.dp
