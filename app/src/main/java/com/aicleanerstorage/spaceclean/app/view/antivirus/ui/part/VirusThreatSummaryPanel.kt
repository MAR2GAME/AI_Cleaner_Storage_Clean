package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun VirusThreatSummaryPanel(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(219.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(44.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = CircleShape,
                            ambientColor = Color(0x40FC8541),
                            spotColor = Color(0x40FC8541),
                        )
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(VirusResultMedium, VirusResultHigh))),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "!",
                    color = Color.White,
                    fontSize = 28.sp,
                    lineHeight = 31.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.in_danger),
                modifier = Modifier.width(165.dp),
                color = VirusFigmaText,
                fontSize = 32.sp,
                lineHeight = 39.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text =
                buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = VirusResultMedium,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                    ) {
                        append(count.toString())
                    }
                    append(" ")
                    withStyle(
                        SpanStyle(
                            color = VirusResultSecondary,
                            fontSize = 18.sp,
                        ),
                    ) {
                        append(stringResource(R.string.threats_found))
                    }
                },
            lineHeight = 28.sp,
            textAlign = TextAlign.Center,
        )
    }
}

val VirusResultMedium = Color(0xFFF9B82B)

val VirusResultHigh = Color(0xFFF9582B)

val VirusResultSecondary = VirusFigmaText.copy(alpha = 0.65f)
