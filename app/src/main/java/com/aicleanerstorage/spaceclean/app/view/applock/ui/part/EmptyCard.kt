package com.aicleanerstorage.spaceclean.app.view.applock.ui.part

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.app.view.applock.ui.AppLockCardColor
import com.aicleanerstorage.spaceclean.app.view.applock.ui.AppLockCardRadius
import com.aicleanerstorage.spaceclean.app.view.applock.ui.AppLockSecondaryText

@Composable
internal fun EmptyCard(text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        color = AppLockCardColor,
        shape = RoundedCornerShape(AppLockCardRadius)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = AppLockSecondaryText, fontSize = 16.sp)
        }
    }
}
