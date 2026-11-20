package com.aicleanerstorage.spaceclean.base.permission

import android.content.Context
import android.os.Build
import android.provider.Settings

fun hasOverlayPermission(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.M || runCatching { Settings.canDrawOverlays(context) }.getOrDefault(false)
