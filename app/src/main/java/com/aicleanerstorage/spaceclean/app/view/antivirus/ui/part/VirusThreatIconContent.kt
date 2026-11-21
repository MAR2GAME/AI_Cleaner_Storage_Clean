package com.aicleanerstorage.spaceclean.app.view.antivirus.ui.part

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.drawable.toBitmap

@Composable
internal fun VirusThreatIconContent(
    drawable: Drawable?,
    fallback: Int,
    modifier: Modifier = Modifier,
) {
    if (drawable != null) {
        Image(
            painter =
                remember(drawable) {
                    BitmapPainter(drawable.toBitmap().asImageBitmap())
                },
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Fit,
        )
    } else {
        Image(
            painter = painterResource(fallback),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Fit,
        )
    }
}
