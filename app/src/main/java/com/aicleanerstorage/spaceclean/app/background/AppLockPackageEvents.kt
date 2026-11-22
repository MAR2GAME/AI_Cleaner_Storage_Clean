package com.aicleanerstorage.spaceclean.app.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import com.aicleanerstorage.spaceclean.domain.port.AppLockRepository

/**
 * Keeps the locked package set in sync with installs and uninstalls and asks the
 * runtime to re-evaluate afterwards (an empty set disarms monitoring entirely).
 *
 * Registered at runtime only, so it is active while [AppRuntimeService] is alive.
 */
internal class AppLockPackageEvents(
    private val context: Context,
    private val repository: AppLockRepository,
    private val onPackagesChanged: () -> Unit,
) {
    private var receiver: BroadcastReceiver? = null

    fun register() {
        if (receiver != null) return
        val listener =
            object : BroadcastReceiver() {
                override fun onReceive(
                    receiverContext: Context?,
                    intent: Intent?,
                ) {
                    if (intent?.getBooleanExtra(Intent.EXTRA_REPLACING, false) == true) return
                    val packageName = intent?.data?.schemeSpecificPart
                    if (packageName.isNullOrBlank()) return
                    when (intent.action) {
                        Intent.ACTION_PACKAGE_ADDED -> runCatching { repository.handlePackageAdded(packageName) }
                        Intent.ACTION_PACKAGE_REMOVED -> runCatching { repository.setPackageLocked(packageName, false) }
                        else -> return
                    }
                    onPackagesChanged()
                }
            }
        val filter =
            IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addDataScheme("package")
            }
        receiver =
            runCatching {
                ContextCompat.registerReceiver(context, listener, filter, ContextCompat.RECEIVER_EXPORTED)
                listener
            }.getOrNull()
    }

    fun unregister() {
        receiver?.let { registered -> runCatching { context.unregisterReceiver(registered) } }
        receiver = null
    }
}
