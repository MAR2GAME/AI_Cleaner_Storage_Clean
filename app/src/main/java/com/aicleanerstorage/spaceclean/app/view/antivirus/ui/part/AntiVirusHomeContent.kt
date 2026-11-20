package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun AntiVirusHomeContent(
    onDeepScan: () -> Unit,
    onQuickScan: () -> Unit,
    onBack: () -> Unit,
    enabled: Boolean = true,
) {
    VirusScanPanel(onBack = onBack) {
        VirusScanBackgroundContent(modifier = Modifier.align(Alignment.TopCenter)) {
            Image(
                painter = painterResource(R.drawable.virus_engine_figma),
                contentDescription = null,
                modifier = Modifier.size(98.dp),
                contentScale = ContentScale.Fit,
            )
        }

        Text(
            text = stringResource(R.string.engine_is_ready),
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 398.dp),
            color = VirusFigmaText,
            fontSize = 20.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.Normal,
        )

        VirusScanFeatureCard(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 468.dp),
        )

        VirusScanActionBar(
            text = stringResource(R.string.deep_scan),
            onClick = onDeepScan,
            filled = true,
            enabled = enabled,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 583.dp),
        )
        VirusScanActionBar(
            text = stringResource(R.string.quick_scan),
            onClick = onQuickScan,
            filled = false,
            enabled = enabled,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 647.dp),
        )
    }
}
