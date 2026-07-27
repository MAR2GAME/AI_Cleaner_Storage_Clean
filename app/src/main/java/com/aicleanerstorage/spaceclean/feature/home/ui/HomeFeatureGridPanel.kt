package com.aicleanerstorage.spaceclean.feature.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.common.feature.FeatureKey
import com.aicleanerstorage.spaceclean.common.ui.components.cleanXPressable

private val HomeFeatureTextColor = Color(0xFF282927)
private val HomeFeatureIconContainerSize = 44.dp
private val HomeFeatureIconTextSpacing = 5.dp
private val HomeFeatureLabelHeight = 38.dp
private val HomeFeatureLabelStyle =
    TextStyle(
        color = HomeFeatureTextColor,
        fontSize = 16.sp,
        lineHeight = 19.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
    )

@Composable
internal fun HomeFeatureGridPanel(
    title: String,
    features: List<FeatureKey>,
    iconSize: (FeatureKey) -> DpSize,
    onFeatureClick: (FeatureKey) -> Unit,
    modifier: Modifier = Modifier,
    titleRes: (FeatureKey) -> Int = { it.titleRes() },
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
        ) {
            Text(
                text = title,
                color = HomeFeatureTextColor,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                thickness = 0.5.dp,
                color = Color(0xFFE5E5E5),
            )
            Spacer(modifier = Modifier.height(20.dp))
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val itemWidth = minOf(102.dp, maxWidth / 3)
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    features.chunked(3).forEach { rowFeatures ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            rowFeatures.forEach { feature ->
                                HomeFeatureGridItem(
                                    feature = feature,
                                    title = stringResource(titleRes(feature)),
                                    iconSize = iconSize(feature),
                                    textWidth = itemWidth,
                                    modifier = Modifier.width(itemWidth),
                                    onClick = { onFeatureClick(feature) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeFeatureGridItem(
    feature: FeatureKey,
    title: String,
    iconSize: DpSize,
    textWidth: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .height(103.dp)
                .cleanXPressable(
                    pressedAlpha = 0.9f,
                    pressedScale = 0.985f,
                    onClick = onClick,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(HomeFeatureIconContainerSize),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(feature.iconRes()),
                contentDescription = title,
                modifier = Modifier.size(width = iconSize.width, height = iconSize.height),
            )
        }
        Spacer(modifier = Modifier.height(HomeFeatureIconTextSpacing))
        Text(
            text = title,
            modifier = Modifier.width(textWidth).height(HomeFeatureLabelHeight),
            style = HomeFeatureLabelStyle,
            softWrap = true,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
