package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.base.designsystem.component.animations.CleanSpiralAnimation

@Composable
internal fun VirusScanBackgroundContent(
    modifier: Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    CleanSpiralAnimation(
        modifier =
            modifier.offset(y = 103.dp),
        containerSize = 268.dp,
        centerSize = 200.dp,
        spiralResId = R.drawable.junk_scan_spiral_figma,
        content = content,
    )
}
