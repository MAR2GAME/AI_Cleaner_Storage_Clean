package com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part.NetworkSpeedGreen
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part.NetworkSpeedGreenDeep
import com.aicleanerstorage.spaceclean.app.view.networkspeed.ui.part.NetworkSpeedGreenLight
import com.aicleanerstorage.spaceclean.base.designsystem.component.animations.SemiCircularGauge

@Composable
internal fun NetworkSpeedGaugePanel(isAnimating: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        SemiCircularGauge(
            modifier = Modifier.size(280.dp),
            isAnimating = isAnimating,
            arcStartColor = NetworkSpeedGreenLight,
            arcEndColor = NetworkSpeedGreen,
            needleColor = NetworkSpeedGreenDeep,
            tickColor = NetworkSpeedGreen.copy(alpha = 0.26f),
        )
    }
}
