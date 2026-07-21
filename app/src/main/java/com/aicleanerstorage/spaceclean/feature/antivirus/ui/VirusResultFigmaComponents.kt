package com.aicleanerstorage.spaceclean.feature.antivirus.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R

internal val VirusResultGreen = Color(0xFF5DD595)
internal val VirusResultMedium = Color(0xFFF9B82B)
internal val VirusResultHigh = Color(0xFFF9582B)
internal val VirusResultSecondary = VirusFigmaText.copy(alpha = 0.65f)

@Composable
internal fun VirusSafeBadge(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(Color(0xFFD0FD91), VirusResultGreen))),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_virus_safe_check_figma),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
        )
    }
}

@Composable
internal fun VirusDangerSummary(
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

@Composable
internal fun VirusRiskCard(
    title: String,
    description: String,
    @StringRes riskLabelRes: Int,
    riskColor: Color,
    height: Dp,
    bodyHeight: Dp,
    bodyStartPadding: Dp,
    descriptionMaxLines: Int,
    onSolve: () -> Unit,
    modifier: Modifier = Modifier,
    bodyIcon: @Composable () -> Unit,
) {
    val mediumLayout = height == 154.dp
    val horizontalPadding = if (mediumLayout) 10.dp else 16.dp
    val verticalPadding = if (mediumLayout) 16.dp else 20.dp

    Column(
        modifier =
            modifier
                .width(343.dp)
                .height(height)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VirusRiskTriangleIcon(
                sourceRes = if (riskColor == VirusResultMedium) R.mipmap.ic_medium else R.mipmap.ic_high,
                color = riskColor,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(riskLabelRes),
                color = riskColor,
                fontSize = 18.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.solve),
                color = VirusResultGreen,
                fontSize = 18.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable(onClick = onSolve),
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(bodyHeight)
                    .padding(start = bodyStartPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            bodyIcon()
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = VirusFigmaText,
                    fontSize = 20.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = description,
                    color = VirusResultSecondary,
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                    maxLines = descriptionMaxLines,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun VirusRiskTriangleIcon(
    @DrawableRes sourceRes: Int,
    color: Color,
) {
    Box(
        modifier = Modifier.size(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(sourceRes),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = "!",
            color = Color.White,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
