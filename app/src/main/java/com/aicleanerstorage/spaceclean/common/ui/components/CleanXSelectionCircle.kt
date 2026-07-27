package com.aicleanerstorage.spaceclean.common.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun CleanXSelectionCircle(
    selected: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: Dp = 24.dp,
    @DrawableRes unselectedIconRes: Int = R.drawable.ic_unselected,
) {
    Image(
        painter = painterResource(if (selected) R.drawable.ic_selected else unselectedIconRes),
        contentDescription = null,
        modifier =
            modifier
                .size(size)
                .alpha(if (enabled) 1f else 0.35f),
    )
}
