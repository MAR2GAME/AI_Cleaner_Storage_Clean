package com.aicleanerstorage.spaceclean.app.platform.storage.preferences

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    internal val store: SharedPreferences =
        context.applicationContext.getSharedPreferences(
            APP_PREFERENCES_NAME,
            Context.MODE_PRIVATE,
        )
}

private const val APP_PREFERENCES_NAME = "quick_clean_settings"
