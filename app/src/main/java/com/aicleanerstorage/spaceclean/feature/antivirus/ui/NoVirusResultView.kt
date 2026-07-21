package com.aicleanerstorage.spaceclean.feature.antivirus.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.navigation.AppDestination
import com.aicleanerstorage.spaceclean.common.ui.components.ToolFeatureBanners

@Composable
internal fun NoVirusResultView(
    onNavigateTool: (AppDestination) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    VirusFigmaPage(
        modifier = modifier,
        onBack = onBack,
    ) {
        VirusSafeBadge(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 175.dp),
        )
        Text(
            text = stringResource(R.string.no_threats_found),
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 231.dp),
            color = androidx.compose.ui.graphics.Color(0xFF999999),
            fontSize = 20.sp,
            lineHeight = 23.sp,
        )

        ToolFeatureBanners(
            onNavigateTool = onNavigateTool,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 284.dp)
                    .width(343.dp),
        )
    }
}
