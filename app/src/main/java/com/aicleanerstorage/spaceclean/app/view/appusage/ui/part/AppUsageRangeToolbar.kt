package com.aicleanerstorage.spaceclean.app.view.appusage.ui.part

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.app.view.appusage.interaction.AppUsageDateRange

@Composable
internal fun AppUsageRangeToolbar(
    selectedRange: AppUsageDateRange,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onRangeSelected: (AppUsageDateRange) -> Unit,
) {
    Box {
        Surface(
            modifier =
                Modifier
                    .widthIn(max = 154.dp)
                    .height(32.dp)
                    .clickable { onExpandedChange(!expanded) },
            color = PrimaryGreen,
            shape = RoundedCornerShape(4.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(selectedRange.labelRes),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.White,
                    modifier =
                        Modifier
                            .size(24.dp)
                            .rotate(if (expanded) 180f else 0f),
                )
            }
        }
        AppUsageRangeMenu(
            expanded = expanded,
            selectedRange = selectedRange,
            onDismiss = { onExpandedChange(false) },
            onRangeSelected = { range ->
                onRangeSelected(range)
                onExpandedChange(false)
            },
        )
    }
}

internal val CardBg = Color.White

internal val Navy = Color(0xFF282927)

internal val PrimaryGreen = Color(0xFF5DD595)

@Composable
private fun AppUsageRangeMenu(
    expanded: Boolean,
    selectedRange: AppUsageDateRange,
    onDismiss: () -> Unit,
    onRangeSelected: (AppUsageDateRange) -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        offset = DpOffset((-96).dp, 4.dp),
        modifier =
            Modifier
                .width(188.dp)
                .height(188.dp)
                .background(CardBg),
        shape = RoundedCornerShape(12.dp),
        containerColor = CardBg,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .width(188.dp)
                    .height(188.dp)
                    .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            AppUsageDateRange.entries.forEachIndexed { index, range ->
                AppUsageRangeMenuItem(
                    range = range,
                    selected = range == selectedRange,
                    onClick = { onRangeSelected(range) },
                )
                if (index != AppUsageDateRange.entries.lastIndex) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFD9DEE6)),
                    )
                }
            }
        }
    }
}

@Composable
private fun AppUsageRangeMenuItem(
    range: AppUsageDateRange,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(21.dp)
                .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(range.labelRes),
            color = Navy,
            fontSize = 18.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(24.dp),
            )
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}
