package com.aicleanerstorage.spaceclean.app.commercial

import com.aicleanerstorage.spaceclean.app.commercial.policy.FeatureEventAdPolicy
import com.aicleanerstorage.spaceclean.common.ads.*
import com.aicleanerstorage.spaceclean.common.commercial.InterstitialRequest
import com.aicleanerstorage.spaceclean.common.feature.*
import com.aicleanerstorage.spaceclean.core.presentation.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AdInterceptor(
    private val driver: AdDriver,
    private val timeoutMillis: Long = 30_000L,
    private val policy: FeatureEventAdPolicy = FeatureEventAdPolicy(),
    private val canShow: (InterstitialRequest) -> Boolean = { true },
    private val onBusyChanged: (Boolean) -> Unit = {},
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate,
) : IntentInterceptor {
    private val mutex = Mutex()
    private val requests = mutableMapOf<String, Job>()
    private var disposed = false

    override suspend fun intercept(policy: IntentPolicy): InterceptResult {
        if (policy != AdNeed.JunkCleanCompletion) return InterceptResult.NotHandled
        this.policy.request(FeatureOperationEvent.OperationFinished(FeatureKey.JUNK_CLEAN, OperationAction.CLEAN, true))?.let { show(it) }
        return InterceptResult.Continue
    }

    suspend fun show(request: InterstitialRequest): Unit = withContext(dispatcher) {
        currentCoroutineContext().ensureActive()
        if (disposed) throw CancellationException("Ad host disposed")
        if (requests.containsKey(request.requestId)) return@withContext
        val owner = currentCoroutineContext().job
        requests[request.requestId] = owner
        try {
            withTimeoutOrNull(timeoutMillis) {
                mutex.withLock {
                    currentCoroutineContext().ensureActive()
                    val area = AdPlacementRegistry.interstitialArea(request.scene) ?: return@withLock
                    if (!driver.isAvailable() || !canShow(request)) return@withLock
                    try {
                        onBusyChanged(true)
                        driver.play(area)
                        currentCoroutineContext().ensureActive()
                        driver.preload(AdPreloadStage.InterstitialClosed)
                    } finally {
                        onBusyChanged(false)
                    }
                }
            }
        } catch (error: CancellationException) { throw error }
        catch (_: Exception) { currentCoroutineContext().ensureActive() }
        finally { if (requests[request.requestId] === owner) requests.remove(request.requestId) }
        currentCoroutineContext().ensureActive()
    }

    fun dispose() {
        disposed = true
        requests.values.toList().forEach { it.cancel(CancellationException("Ad host disposed")) }
    }
}
