package com.aicleanerstorage.spaceclean.app

internal fun interface OpenAdHostWatchdogCancellation {
    fun cancel()
}

internal class OpenAdHostWatchdog(
    private val timeoutMillis: Long,
    private val schedule: (Long, () -> Unit) -> OpenAdHostWatchdogCancellation,
    private val onTimeout: () -> Unit,
) {
    private var adActive = false
    private var hasWindowFocus = false
    private var disposed = false
    private var generation = 0L
    private var pending: OpenAdHostWatchdogCancellation? = null

    fun onAdStateChanged(active: Boolean) {
        adActive = active
        refresh()
    }

    fun onWindowFocusChanged(hasFocus: Boolean) {
        hasWindowFocus = hasFocus
        refresh()
    }

    fun dispose() {
        if (disposed) return
        disposed = true
        generation += 1
        pending?.cancel()
        pending = null
    }

    private fun refresh() {
        if (disposed) return
        generation += 1
        pending?.cancel()
        pending = null
        if (!adActive || !hasWindowFocus) return

        val scheduledGeneration = generation
        pending =
            schedule(timeoutMillis) {
                if (disposed || scheduledGeneration != generation || !adActive || !hasWindowFocus) {
                    return@schedule
                }
                pending = null
                generation += 1
                onTimeout()
            }
    }
}
