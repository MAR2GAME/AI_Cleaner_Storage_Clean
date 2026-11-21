package com.aicleanerstorage.spaceclean.app.background

import android.content.Context
import android.content.Intent
import com.aicleanerstorage.spaceclean.app.view.applock.ui.part.LockOverlayView
import com.aicleanerstorage.spaceclean.data.applock.AppLockMonitoringRule
import com.aicleanerstorage.spaceclean.data.applock.AppLockRepository
import com.aicleanerstorage.spaceclean.data.appusage.AppUsageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The running half of App Lock: polls the foreground app and covers locked apps
 * with the PIN overlay. Hosted by [AppRuntimeService], which owns the process.
 */
class AppLockMonitor(
    private val context: Context,
    private val repository: AppLockRepository,
    private val usageRepository: AppUsageRepository,
    private val scope: CoroutineScope,
) {
    private val policy = PinAttemptPolicy()

    private var pollJob: Job? = null
    private var overlay: LockOverlayView? = null
    private var lastForegroundPackage: String? = null

    val isArmed: Boolean
        get() = runCatching { AppLockMonitoringRule.shouldMonitorApps(repository) }.getOrDefault(false)

    fun sync() {
        if (isArmed) startPolling() else stop()
    }

    fun stop() {
        pollJob?.cancel()
        pollJob = null
        dismissOverlay()
        lastForegroundPackage = null
    }

    private fun startPolling() {
        if (pollJob?.isActive == true) return
        pollJob =
            scope.launch {
                while (isActive) {
                    if (!isArmed) {
                        dismissOverlay()
                        break
                    }
                    if (overlay == null) pollOnce()
                    delay(POLL_INTERVAL_MS)
                }
            }
    }

    private suspend fun pollOnce() {
        val packageName = usageRepository.foregroundPackage() ?: return
        if (packageName == context.packageName || packageName == lastForegroundPackage) return
        lastForegroundPackage = packageName
        if (repository.isPackageLocked(packageName)) showOverlay(packageName)
    }

    private fun showOverlay(targetPackage: String) {
        if (overlay != null) return
        policy.reset()
        val view =
            LockOverlayView(
                context = context,
                repository = repository,
                scope = scope,
                onPinComplete = ::verifyPin,
                onCancel = {
                    dismissOverlay()
                    goHome()
                },
            )
        overlay = view.takeIf { it.show(targetPackage) }
    }

    private fun verifyPin(pin: String) {
        scope.launch {
            val matches = withContext(Dispatchers.Default) { repository.verifyPin(pin) }
            val view = overlay ?: return@launch
            val now = System.currentTimeMillis()
            when {
                matches -> dismissOverlay()
                policy.registerFailure(now) -> view.showLockout(policy.remainingSeconds(now))
                else -> view.showError()
            }
        }
    }

    private fun dismissOverlay() {
        overlay?.dismiss()
        overlay = null
    }

    private fun goHome() {
        runCatching {
            context.startActivity(
                Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_HOME)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                },
            )
        }
    }

    private companion object {
        const val POLL_INTERVAL_MS = 500L
    }
}
