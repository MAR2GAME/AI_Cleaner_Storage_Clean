package com.aicleanerstorage.spaceclean.app.view.home.ui.part

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun HomeBackgroundContent() {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F6F6)),
    ) {
        Image(
            painter = painterResource(id = R.drawable.home_bg_figma_bitmap),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxWidth().aspectRatio(375f / 326f),
        )
    }
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
