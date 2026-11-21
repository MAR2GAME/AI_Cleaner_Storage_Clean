package com.aicleanerstorage.spaceclean.app.view.applock.ui.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicleanerstorage.spaceclean.base.designsystem.component.CleanXBlue
import com.aicleanerstorage.spaceclean.base.designsystem.component.rememberPackageIconBitmap

@Composable
internal fun PackageIconContent(
    packageName: String,
    fallbackText: String,
) {
    val bitmap = rememberPackageIconBitmap(packageName)
    if (bitmap != null) {
        Image(
            painter = BitmapPainter(bitmap),
            contentDescription = null,
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape),
        )
    } else {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CleanXBlue.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = fallbackText,
                color = CleanXBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
