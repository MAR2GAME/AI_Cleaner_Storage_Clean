package com.aicleanerstorage.spaceclean.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.aicleanerstorage.spaceclean.BuildConfig

object AppConfig {
    const val TERMS_OF_SERVICE_URL: String = BuildConfig.TERMS_OF_SERVICE_URL
    const val PRIVACY_POLICY_URL: String = BuildConfig.PRIVACY_POLICY_URL

    // ================ Helpers ================
    fun hasPostNotificationsPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            runCatching {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            }.getOrDefault(false)
}
