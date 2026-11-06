package com.aicleanerstorage.spaceclean.core.contracts.ads

import kotlinx.coroutines.*

class ExternalAppOpenGuard(
    private val driver: AdDriver,
    private val scope: CoroutineScope,
    private val returnCooldownMillis: Long = 1_200L,
) {
    private var launchPending = false
    private var cooldown: Job? = null
    var isReturning = false
        private set
    fun onLaunch() {
        launchPending = true
        isReturning = false
        cooldown?.cancel()
        driver.suppressNextAppOpen(true)
    }
    fun onLaunchCancelled() {
        launchPending = false
        isReturning = false
        cooldown?.cancel()
        driver.suppressNextAppOpen(false)
    }
    fun onHostResumed() {
        if (!launchPending) return
        launchPending = false
        isReturning = true
        cooldown?.cancel()
        cooldown = scope.launch {
            delay(returnCooldownMillis)
            isReturning = false
            driver.suppressNextAppOpen(false)
        }
    }
    fun dispose() = onLaunchCancelled()
}
