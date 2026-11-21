package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanUiState
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.resolveVirusIcon
import com.aicleanerstorage.spaceclean.data.security.model.VirusScanMode

@Composable
internal fun VirusScanLoading(
    mode: VirusScanMode,
    uiState: VirusScanUiState,
) {
    VirusScanPanel {
        VirusScanBackgroundContent(modifier = Modifier.align(Alignment.TopCenter)) {
            if (uiState.currentLabel.isBlank()) {
                Image(
                    painter = painterResource(R.drawable.virus_engine_figma),
                    contentDescription = null,
                    modifier = Modifier.size(98.dp),
                    contentScale = ContentScale.Fit,
                )
            } else {
                VirusThreatIconContent(
                    drawable = resolveVirusIcon(uiState.currentIcon),
                    fallback = R.drawable.virus_engine_figma,
                    modifier =
                        Modifier
                            .size(70.dp)
                            .clip(CircleShape),
                )
            }
        }

        Text(
            text = uiState.currentLabel.ifBlank { stringResource(R.string.virus_data_privacy) },
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 398.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            color = VirusFigmaText,
            fontSize = 20.sp,
            lineHeight = 23.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )

        VirusScanProgressBar(
            mode = mode,
            progress = uiState.progressFraction,
            hasAdbRisk = uiState.hasAdbRisk,
            appThreatCount = uiState.appThreatCount,
            fileThreatCount = uiState.fileThreatCount,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 553.dp),
        )
    }
}
