package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.R

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
