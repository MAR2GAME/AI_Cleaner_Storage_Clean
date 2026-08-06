package com.aicleanerstorage.spaceclean.common.ads

internal class ExternalAppOpenGuard(
    private val driver: AdRuntimeDriver,
    private val scheduler: AdRuntimeScheduler,
    private val returnCooldownMillis: Long = DEFAULT_RETURN_COOLDOWN_MS,
) {
    private var launchPending = false
    private var returnCooldown: AdRuntimeCancellation? = null

    @Volatile
    var isReturning: Boolean = false
        private set

    fun onLaunch() {
        launchPending = true
        isReturning = false
        returnCooldown?.cancel()
        returnCooldown = null
        driver.suppressNextAppOpen(true)
    }

    fun onLaunchCancelled() {
        launchPending = false
        isReturning = false
        restoreAppOpen()
    }

    fun onHostResumed() {
        if (!launchPending) return
        launchPending = false
        isReturning = true
        returnCooldown?.cancel()
        returnCooldown = scheduler.schedule(returnCooldownMillis) {
            if (!launchPending) {
                isReturning = false
                restoreAppOpen()
            }
        }
    }

    fun dispose() {
        launchPending = false
        isReturning = false
        returnCooldown?.cancel()
        returnCooldown = null
        restoreAppOpen()
    }

    private fun restoreAppOpen() {
        driver.suppressNextAppOpen(false)
    }

    private companion object {
        const val DEFAULT_RETURN_COOLDOWN_MS = 1_200L
    }
}
