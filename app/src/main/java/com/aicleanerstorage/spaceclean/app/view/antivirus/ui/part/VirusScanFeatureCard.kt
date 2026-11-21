package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun VirusScanFeatureCard(modifier: Modifier = Modifier) {
    val features =
        listOf(
            R.mipmap.ic_virus to R.string.virus,
            R.mipmap.ic_malware to R.string.malware,
            R.mipmap.ic_privacy to R.string.privacy,
        )

    Row(
        modifier =
            modifier
                .width(343.dp)
                .height(87.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        features.forEachIndexed { index, feature ->
            Box(
                modifier =
                    Modifier
                        .width(if (index == 1) 70.dp else 71.dp)
                        .height(55.dp),
                contentAlignment = Alignment.Center,
            ) {
                androidx.compose.foundation.layout.Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(feature.first),
                        contentDescription = null,
                        tint = VirusFigmaText,
                        modifier = Modifier.size(24.dp),
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(feature.second),
                        color = VirusFigmaText,
                        fontSize = 18.sp,
                        lineHeight = 21.sp,
                    )
                }
            }

            if (index < features.lastIndex) {
                Box(
                    modifier =
                        Modifier
                            .width(1.dp)
                            .height(48.dp)
                            .background(VirusFigmaText.copy(alpha = 0.15f)),
                )
            }
        }
    }
}

val VirusFigmaText = Color(0xFF282927)
