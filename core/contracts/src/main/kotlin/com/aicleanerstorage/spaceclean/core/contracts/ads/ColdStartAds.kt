package com.aicleanerstorage.spaceclean.core.contracts.ads

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

suspend fun AdDriver.showColdStart(onOpenAdStateChanged: (Boolean) -> Unit = {}) {
    val wasEnabled = isAppOpenEnabled()
    setAppOpenEnabled(false)
    try {
        if (isAvailable()) {
            try {
                withTimeoutOrNull(6_500L) {
                    val ready = CompletableDeferred<Unit>()
                    if (initializeConsent { ready.complete(Unit) }) ready.complete(Unit)
                    ready.await()
                }
                withTimeoutOrNull(6_500L) {
                    val closed = CompletableDeferred<Unit>()
                    showConsent { closed.complete(Unit) }
                    closed.await()
                }
            } catch (error: CancellationException) { throw error } catch (_: Exception) { }
        }
        try { preload(AdPreloadStage.Startup) } catch (error: CancellationException) { throw error } catch (_: Exception) { }
        if (isAvailable()) awaitColdStartOpen(onOpenAdStateChanged) else onOpenAdStateChanged(false)
    } finally {
        onOpenAdStateChanged(false)
        setAppOpenEnabled(wasEnabled)
    }
}

private suspend fun AdDriver.awaitColdStartOpen(onState: (Boolean) -> Unit): Unit = coroutineScope {
    val finished = CompletableDeferred<Unit>()
    val loaded = AtomicBoolean(false)
    val closed = AtomicBoolean(false)
    val startTimeout = launch { delay(6_500L); if (!loaded.get()) finished.complete(Unit) }
    var closeSettle: Job? = null
    try {
        showOpen(
            onLoaded = {
                if (!finished.isCompleted && isActive && loaded.compareAndSet(false, true)) {
                    startTimeout.cancel()
                    onState(true)
                }
            },
            onClosed = {
                if (!finished.isCompleted && isActive && closed.compareAndSet(false, true)) {
                    closeSettle = launch {
                        if (loaded.get()) delay(800L)
                        finished.complete(Unit)
                    }
                }
            },
        )
        finished.await()
    } catch (error: CancellationException) { throw error } catch (_: Exception) { }
    finally {
        finished.cancel()
        startTimeout.cancel()
        closeSettle?.cancel()
        onState(false)
    }
}
