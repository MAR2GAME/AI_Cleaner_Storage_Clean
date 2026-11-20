package com.aicleanerstorage.spaceclean.app.view.applock.ui.part

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.app.view.applock.ui.AppLockNavy
import com.aicleanerstorage.spaceclean.data.applock.model.AppLockAppModel

@Composable
internal fun AppLockRow(
    app: AppLockAppModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        PackageIconContent(
            packageName = app.packageName,
            fallbackText = app.appName.take(1).ifBlank { "A" }
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = app.appName,
            color = AppLockNavy,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        AppLockCheckContent(checked = app.isLocked)
    }
}
