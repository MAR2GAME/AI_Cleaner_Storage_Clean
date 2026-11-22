package com.aicleanerstorage.spaceclean.app.view.common.component

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 瀛樺偍鐢ㄩ噺涓庢€婚噺鏂囨湰锛涙寜鍙敤瀹藉害鍔ㄦ€佺缉鏀剧敤閲忓瓧鍙枫€? *
 * @param usedText 宸茬敤瀹归噺鏂囨湰銆? * @param totalText 鎬诲閲忔枃鏈紝鍚墠缂€銆? * @param totalTextStyle 鎬婚噺鏂囨湰鏍峰紡銆? * @param color 鐢ㄩ噺鏂囨湰棰滆壊銆? */
@Composable
internal fun RowScope.StorageValueText(
    usedText: String,
    totalText: String,
    totalTextStyle: TextStyle,
    color: androidx.compose.ui.graphics.Color,
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    BoxWithConstraints(modifier = Modifier.weight(1f)) {
        val maxWidthPx = with(density) { maxWidth.roundToPx() }
        val totalWidthPx =
            remember(totalText, textMeasurer) {
                textMeasurer.measure(totalText, totalTextStyle, maxLines = 1).size.width
            }.coerceAtMost(maxWidthPx)
        val usedWidthPx = (maxWidthPx - totalWidthPx).coerceAtLeast(0)
        val usedFontSize =
            remember(usedText, usedWidthPx, textMeasurer) {
                (24 downTo 16)
                    .firstOrNull { size ->
                        textMeasurer
                            .measure(
                                text = usedText,
                                style = TextStyle(fontSize = size.sp, fontWeight = FontWeight.Bold),
                                maxLines = 1,
                            ).size.width <= usedWidthPx
                    }?.sp ?: 16.sp
            }
        val totalWidth = with(density) { totalWidthPx.toDp() }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = usedText,
                color = color,
                fontSize = usedFontSize,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = totalText,
                style = totalTextStyle,
                modifier = Modifier.width(totalWidth).padding(bottom = 2.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

