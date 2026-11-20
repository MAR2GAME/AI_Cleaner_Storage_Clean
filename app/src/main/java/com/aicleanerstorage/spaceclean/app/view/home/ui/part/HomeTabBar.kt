package com.aicleanerstorage.spaceclean.app.view.home.ui.part

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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun HomeTabBar(
    tabs: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().height(43.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            Column(
                modifier = Modifier.clickable { onSelected(index) }.padding(start = 16.dp, top = 8.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    color = if (selected) Color(0xFF282927) else Color(0xFF999999),
                    fontSize = 18.sp,
                    lineHeight = 22.sp,
                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier =
                        Modifier
                            .width(
                                when (index) {
                                    1 -> 89.dp
                                    2 -> 60.dp
                                    else -> 39.dp
                                },
                            )
                            .height(2.dp)
                            .background(if (selected) Color(0xFF5DD595) else Color.Transparent),
                )
            }
        }
    }
}
