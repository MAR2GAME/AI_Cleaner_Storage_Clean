package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R
import com.aicleanerstorage.spaceclean.app.view.antivirus.interaction.VirusScanUiState
import com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part.resolveVirusIcon
import com.aicleanerstorage.spaceclean.data.security.model.VirusThreatModel

@Composable
internal fun VirusThreatResultContent(
    uiState: VirusScanUiState,
    onSolveAdbRisk: () -> Unit,
    onSolveThreat: (VirusThreatModel) -> Unit,
    onBack: () -> Unit,
) {
    VirusScanPanel(onBack = onBack) {
        VirusThreatSummaryPanel(
            count = uiState.effectiveThreatCount,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 176.dp),
        )

        Column(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 292.dp)
                    .width(343.dp)
                    .height(520.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            if (uiState.hasAdbRisk) {
                VirusRiskCard(
                    title = stringResource(R.string.usb_debugging_enabled),
                    description = stringResource(R.string.adb_hint),
                    riskLabelRes = R.string.medium_risk,
                    riskColor = VirusResultMedium,
                    height = 154.dp,
                    bodyHeight = 86.dp,
                    bodyStartPadding = 12.dp,
                    descriptionMaxLines = 3,
                    onSolve = onSolveAdbRisk,
                    bodyIcon = {
                        Image(
                            painter = painterResource(R.drawable.ic_virus_result_usb_figma),
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                        )
                    },
                )
                if (uiState.threats.isNotEmpty()) Spacer(modifier = Modifier.height(20.dp))
            }

            uiState.threats.forEachIndexed { index, threat ->
                VirusRiskCard(
                    title = threat.title.ifBlank { stringResource(R.string.threat_file) },
                    description = threat.description.ifBlank { stringResource(R.string.high_risk) },
                    riskLabelRes = R.string.high_risk,
                    riskColor = VirusResultHigh,
                    height = 124.dp,
                    bodyHeight = 48.dp,
                    bodyStartPadding = 0.dp,
                    descriptionMaxLines = 1,
                    onSolve = { onSolveThreat(threat) },
                    bodyIcon = { VirusThreatResultIcon(threat) },
                )
                if (index < uiState.threats.lastIndex) Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun VirusThreatResultIcon(threat: VirusThreatModel) {
    if (threat.icon != null) {
        VirusThreatIconContent(
            drawable = resolveVirusIcon(threat.icon),
            fallback = R.mipmap.ic_virus_small,
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.8.dp)),
        )
    } else {
        Box(
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.8.dp))
                    .background(Color(0x0DFC4141)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.mipmap.ic_virus_small),
                contentDescription = null,
                tint = VirusResultHigh,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}
