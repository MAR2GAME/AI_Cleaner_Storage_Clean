package com.aicleanerstorage.spaceclean.app.view.antivirus.ui

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.aicleanerstorage.spaceclean.R

@Composable
internal fun resolveVirusIcon(reference: String?): Drawable? {
    val context = LocalContext.current
    return remember(context, reference) {
        runCatching {
            if (reference == "@protection") context.getDrawable(R.drawable.virus_engine_figma)
            else reference?.let(context.packageManager::getApplicationIcon)
        }.getOrNull()
    }
}
