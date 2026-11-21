package com.aicleanerstorage.spaceclean.app.view.applock.ui.part

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.app.view.applock.ui.part.AppLockCardColor
import com.aicleanerstorage.spaceclean.app.view.applock.ui.part.AppLockCardRadius
import com.aicleanerstorage.spaceclean.app.view.applock.ui.part.AppLockNavy

@Composable
internal fun CheckActionCard(
    title: String,
    checked: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(55.dp)
                .clickable { onClick() },
        color = AppLockCardColor,
        shape = RoundedCornerShape(AppLockCardRadius),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                color = AppLockNavy,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f),
            )
            AppLockCheckContent(checked = checked)
        }
    }
}
